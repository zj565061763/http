package com.sd.lib.http

import com.sd.lib.http.callback.IUploadProgressCallback
import com.sd.lib.http.callback.RequestCallback
import com.sd.lib.http.utils.HttpLog
import com.sd.lib.http.utils.TransmitParam
import kotlinx.coroutines.*

/** 单线程调度器 */
private val singleThreadContext: ExecutorCoroutineDispatcher by lazy {
    newSingleThreadContext("FHttp single thread")
}

internal abstract class RequestTask : IUploadProgressCallback {
    private val mRequest: IRequest
    private val mRequestCallback: RequestCallback

    private val mMainScope = MainScope()
    private var mIsSubmitted: Boolean = false

    constructor(request: IRequest, requestCallback: RequestCallback) {
        mRequest = request
        mRequestCallback = requestCallback
        request.uploadProgressCallback = this
    }

    private val logPrefix: String
        private get() = this.toString()

    /**
     * 提交任务，任务按顺序一个个执行
     */
    fun submitSequence() {
        submitInternal(true)
    }

    /**
     * 提交任务
     */
    fun submit() {
        submitInternal(false)
    }

    @Synchronized
    private fun submitInternal(sequence: Boolean) {
        if (mIsSubmitted) return
        mIsSubmitted = true

        val dispatcher = if (sequence) singleThreadContext else Dispatchers.IO
        mMainScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    mRequestCallback.onStart()
                    HttpLog.i("$logPrefix onStart ${Thread.currentThread().name}")
                }

                withContext(dispatcher) {
                    val response = mRequest.execute()
                    HttpLog.i("$logPrefix execute ${Thread.currentThread().name}")

                    mRequestCallback.response = response
                    mRequestCallback.onSuccessBackground()
                    HttpLog.i("$logPrefix onSuccessBackground ${Thread.currentThread().name}")
                }

                withContext(Dispatchers.Main) {
                    mRequestCallback.onSuccessBefore()
                    HttpLog.i("$logPrefix onSuccessBefore  ${Thread.currentThread().name}")
                }

                withContext(Dispatchers.Main) {
                    mRequestCallback.onSuccess()
                    HttpLog.i("$logPrefix onSuccess  ${Thread.currentThread().name}")
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    mRequestCallback.onCancel()
                    HttpLog.i("$logPrefix onCancel  ${Thread.currentThread().name}")
                    // 如果是取消异常，让其继续传播
                    throw e
                } else {
                    mRequestCallback.onError(e)
                    HttpLog.i("$logPrefix onError:$e  ${Thread.currentThread().name}")
                }
            } finally {
                mRequestCallback.onFinish()
                this@RequestTask.onFinish()
                HttpLog.i("$logPrefix onFinish ${Thread.currentThread().name}")
            }
        }
    }

    /**
     * 取消任务
     * @return true-任务被取消
     */
    @Synchronized
    fun cancel(): Boolean {
        val job = mMainScope
        if (!job.isActive) {
            return false
        }

        HttpLog.e("$logPrefix cancel start")

        job.cancel()
        val isActive = job.isActive
        val isCancelled = !isActive

        HttpLog.e("$logPrefix cancel finish isActive:${isActive}")
        return isCancelled
    }

    abstract fun onFinish()

    override fun onProgressUpload(param: TransmitParam) {
        mRequestCallback.onProgressUpload(param)
    }
}
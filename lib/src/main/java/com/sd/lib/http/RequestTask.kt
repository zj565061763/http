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
            notifyStart()

            if (!isActive) {
                notifyCancel()
                return@launch
            }

            try {
                withContext(dispatcher) {
                    HttpLog.i("$logPrefix execute ${Thread.currentThread().name}")
                    val response = mRequest.execute()

                    HttpLog.i("$logPrefix onSuccessBackground ${Thread.currentThread().name}")
                    mRequestCallback.response = response
                    mRequestCallback.onSuccessBackground()
                }
            } catch (e: Exception) {
                if (e is CancellationException) {
                    notifyCancel()
                    throw e
                } else {
                    notifyError(e)
                    return@launch
                }
                throw RuntimeException("unreachable code")
            }

            HttpLog.i("$logPrefix onSuccessBefore  ${Thread.currentThread().name}")
            mRequestCallback.onSuccessBefore()

            if (!isActive) {
                notifyCancel()
                return@launch
            }

            notifySuccess()
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

        job.cancel()
        val isActive = job.isActive
        val isCancelled = !isActive

        HttpLog.e("$logPrefix cancel isActive:${isActive}")
        return isCancelled
    }

    private fun notifyStart() {
        HttpLog.i("$logPrefix onStart ${Thread.currentThread().name}")
        mRequestCallback.onStart()
    }

    private fun notifyError(e: Exception) {
        HttpLog.i("$logPrefix onError:$e  ${Thread.currentThread().name}")
        mRequestCallback.onError(e)
        notifyFinish()
    }

    private fun notifyCancel() {
        HttpLog.i("$logPrefix onCancel  ${Thread.currentThread().name}")
        mRequestCallback.onCancel()
        notifyFinish()
    }

    private fun notifySuccess() {
        HttpLog.i("$logPrefix onSuccess  ${Thread.currentThread().name}")
        mRequestCallback.onSuccess()
        notifyFinish()
    }

    private fun notifyFinish() {
        HttpLog.i("$logPrefix onFinish ${Thread.currentThread().name}")
        mRequestCallback.onFinish()
        this@RequestTask.onFinish()
    }

    abstract fun onFinish()

    override fun onProgressUpload(param: TransmitParam) {
        mRequestCallback.onProgressUpload(param)
    }
}
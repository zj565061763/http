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
    private var mIsSubmitted = false

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

        mMainScope.launch {
            try {
                val dispatcher = if (sequence) singleThreadContext else Dispatchers.IO
                withContext(dispatcher) {
                    withContext(Dispatchers.Main) {
                        // 通知开始
                        HttpLog.i("$logPrefix onStart")
                        mRequestCallback.onStart()
                    }

                    try {
                        val response = mRequest.execute()
                        HttpLog.i("$logPrefix execute")

                        if (!isActive) return@withContext
                        mRequestCallback.response = response
                        mRequestCallback.onSuccessBackground()
                        HttpLog.i("$logPrefix onSuccessBackground")
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            mRequestCallback.onError(e)
                            HttpLog.i("$logPrefix onError:$e")
                        }
                    }

                    withContext(Dispatchers.Main) {
                        if (!isActive) return@withContext
                        mRequestCallback.onSuccessBefore()
                        HttpLog.i("$logPrefix onSuccessBefore")

                        if (!isActive) return@withContext
                        mRequestCallback.onSuccess()
                        HttpLog.i("$logPrefix onSuccess")
                    }
                }
            } catch (e: CancellationException) {
                mRequestCallback.onCancel()
                HttpLog.i("$logPrefix onCancel")
            } finally {
                mRequestCallback.onFinish()
                this@RequestTask.onFinish()
                HttpLog.i("$logPrefix onFinish")
            }
        }
    }

    /**
     * 取消任务
     * @return true-任务被取消
     */
    @Synchronized
    fun cancel(): Boolean {
        var isActive = mMainScope.isActive
        HttpLog.e("$logPrefix cancel start isActive:${isActive}")
        if (isActive) {
            mMainScope.cancel()
            isActive = mMainScope.isActive
        }
        HttpLog.e("$logPrefix cancel finish isActive:${isActive}")
        return !isActive
    }

    abstract fun onFinish()

    override fun onProgressUpload(param: TransmitParam) {
        mRequestCallback.onProgressUpload(param)
    }
}
package com.sd.lib.http

import com.sd.lib.http.callback.IUploadProgressCallback
import com.sd.lib.http.callback.RequestCallback
import com.sd.lib.http.exception.HttpException
import com.sd.lib.http.utils.HttpLog
import com.sd.lib.http.utils.TransmitParam
import kotlinx.coroutines.*

internal abstract class RequestTask : IRequestTaskApi, IUploadProgressCallback {
    private val mRequest: IRequest
    private val mRequestCallback: RequestCallback

    private var mJob: Job? = null
    private var mIsSubmitted: Boolean = false

    constructor(request: IRequest, requestCallback: RequestCallback) {
        mRequest = request
        mRequestCallback = requestCallback
        request.uploadProgressCallback = this
    }

    private val logPrefix: String
        get() = this.toString()

    override val isActive: Boolean
        get() {
            val job = mJob ?: return false
            return job.isActive
        }

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

        mJob = GlobalScope.launch(Dispatchers.Main) {
            notifyStart()

            if (!isActive) {
                notifyCancel()
                return@launch
            }

            val dispatcher = if (sequence) singleThreadContext else Dispatchers.IO
            try {
                withContext(dispatcher) {
                    HttpLog.i("$logPrefix execute ${Thread.currentThread()}")
                    val response = mRequest.execute()

                    HttpLog.i("$logPrefix onSuccessBackground ${Thread.currentThread()}")
                    mRequestCallback.saveResponse(response)
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
            }

            HttpLog.i("$logPrefix onSuccessBefore  ${Thread.currentThread()}")
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
        val job = mJob ?: return false
        if (!job.isActive) return false

        job.cancel()
        val isActive = job.isActive
        val isCancelled = !isActive

        HttpLog.e("$logPrefix cancel isActive:${isActive}")
        return isCancelled
    }

    private fun notifyStart() {
        HttpLog.i("$logPrefix onStart ${Thread.currentThread()}")
        mRequestCallback.onStart()
    }

    private fun notifyError(e: Exception) {
        require(e !is CancellationException)
        HttpLog.i("$logPrefix onError:$e  ${Thread.currentThread()}")
        mRequestCallback.onError(HttpException.wrap(e))
        notifyFinish()
    }

    private fun notifyCancel() {
        HttpLog.i("$logPrefix onCancel  ${Thread.currentThread()}")
        mRequestCallback.onCancel()
        notifyFinish()
    }

    private fun notifySuccess() {
        HttpLog.i("$logPrefix onSuccess  ${Thread.currentThread()}")
        mRequestCallback.onSuccess()
        notifyFinish()
    }

    private fun notifyFinish() {
        HttpLog.i("$logPrefix onFinish ${Thread.currentThread()}")
        mRequestCallback.onFinish()
        this@RequestTask.onFinish()
    }

    abstract fun onFinish()

    override fun onProgressUpload(param: TransmitParam) {
        mRequestCallback.onProgressUpload(param)
    }

    companion object {
        /** 单线程调度器 */
        @JvmStatic
        internal val singleThreadContext: ExecutorCoroutineDispatcher by lazy {
            newSingleThreadContext("FHttp single thread")
        }
    }
}
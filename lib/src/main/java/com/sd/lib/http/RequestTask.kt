package com.sd.lib.http

import com.sd.lib.http.callback.IUploadProgressCallback
import com.sd.lib.http.callback.RequestCallback
import com.sd.lib.http.exception.HttpException
import com.sd.lib.http.utils.HttpLog
import com.sd.lib.http.utils.HttpUtils
import com.sd.lib.http.utils.TransmitParam
import kotlinx.coroutines.*

internal abstract class RequestTask : IUploadProgressCallback {
    private val mRequest: IRequest
    private val mRequestCallback: RequestCallback

    private var mJob: Job? = null

    @Volatile
    private var mIsStartNotified: Boolean = false

    @Volatile
    private var mIsResultNotified: Boolean = false

    constructor(request: IRequest, requestCallback: RequestCallback) {
        mRequest = request
        mRequestCallback = requestCallback
        request.uploadProgressCallback = this
    }

    private val logPrefix: String
        get() = this.toString()

    /**
     * 任务是否处于活动状态
     */
    val isActive: Boolean
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
        if (mJob != null) return

        mJob = GlobalScope.launch(Dispatchers.Main) {
            try {
                // 用withContext包裹代码块，如果代码块中调用了取消，则代码不会继续往下执行，否者需要手动判断是否被取消
                withContext(Dispatchers.Main) {
                    notifyStart()
                }

                try {
                    val dispatcher = if (sequence) singleThreadContext else Dispatchers.IO
                    withContext(dispatcher) {
                        HttpLog.i("$logPrefix execute ${Thread.currentThread()}")
                        val response = mRequest.execute()

                        HttpLog.i("$logPrefix onSuccessBackground ${Thread.currentThread()}")
                        mRequestCallback.saveResponse(response)
                        mRequestCallback.onSuccessBackground()
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) {
                        // 让取消异常继续传播
                        throw e
                    } else {
                        notifyError(e)
                        return@launch
                    }
                }

                withContext(Dispatchers.Main) {
                    HttpLog.i("$logPrefix onSuccessBefore  ${Thread.currentThread()}")
                    mRequestCallback.onSuccessBefore()
                }

                notifySuccess()
            } catch (e: CancellationException) {
                notifyCancel()
            } finally {
                notifyFinish()
            }
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

        HttpLog.e("$logPrefix cancel start mIsStartNotified:${mIsStartNotified} mIsSuccessNotified:${mIsResultNotified}")
        if (mIsResultNotified) return false

        job.cancel()
        val isActive = job.isActive
        val isCancelled = !isActive

        if (isCancelled && !mIsStartNotified) {
            HttpUtils.runOnUiThread {
                notifyCancel()
                notifyFinish()
            }
        }

        HttpLog.e("$logPrefix cancel finish isActive:${isActive}")
        return isCancelled
    }

    private fun notifyStart() {
        HttpLog.i("$logPrefix onStart ${Thread.currentThread()}")
        mIsStartNotified = true
        mRequestCallback.onStart()
    }

    private fun notifyError(e: Exception) {
        require(e !is CancellationException)
        HttpLog.i("$logPrefix onError:$e  ${Thread.currentThread()}")
        mIsResultNotified = true
        mRequestCallback.onError(HttpException.wrap(e))
    }

    private fun notifySuccess() {
        HttpLog.i("$logPrefix onSuccess  ${Thread.currentThread()}")
        mIsResultNotified = true
        mRequestCallback.onSuccess()
    }

    private fun notifyCancel() {
        HttpLog.i("$logPrefix onCancel  ${Thread.currentThread()}")
        mRequestCallback.onCancel()
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
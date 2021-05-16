package com.sd.lib.http

import com.sd.lib.http.callback.IUploadProgressCallback
import com.sd.lib.http.callback.RequestCallback
import com.sd.lib.http.exception.HttpException
import com.sd.lib.http.utils.HttpLog
import com.sd.lib.http.utils.HttpUtils
import com.sd.lib.http.utils.TransmitParam
import kotlinx.coroutines.*

internal abstract class RequestTask : IUploadProgressCallback {
    private val _request: IRequest
    private val _requestCallback: RequestCallback

    /** 任务 */
    private var _job: Job? = null

    @Volatile
    private var _isStartNotified: Boolean = false

    @Volatile
    private var _isResultNotified: Boolean = false

    constructor(request: IRequest, requestCallback: RequestCallback) {
        _request = request
        _requestCallback = requestCallback
        request.uploadProgressCallback = this
    }

    /** 日志前缀 */
    private val _logPrefix: String by lazy { this@RequestTask.toString() }

    /** 任务是否处于活动状态 */
    val isActive: Boolean
        get() {
            val job = _job ?: return false
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
        if (_job != null) return

        _job = GlobalScope.launch(Dispatchers.Main) {
            try {
                // 用withContext包裹代码块，如果代码块中调用了取消，则代码不会继续往下执行，否者需要手动判断是否被取消
                withContext(Dispatchers.Main) {
                    notifyStart()
                }

                try {
                    val dispatcher = if (sequence) singleThreadContext else Dispatchers.IO
                    withContext(dispatcher) {
                        HttpLog.i("$_logPrefix execute ${Thread.currentThread()}")
                        val response = _request.execute()

                        HttpLog.i("$_logPrefix onSuccessBackground ${Thread.currentThread()}")
                        _requestCallback.saveResponse(response)
                        _requestCallback.onSuccessBackground()
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
                    HttpLog.i("$_logPrefix onSuccessBefore  ${Thread.currentThread()}")
                    _requestCallback.onSuccessBefore()
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
        val job = _job ?: return false
        if (!job.isActive) return false

        HttpLog.e("$_logPrefix cancel start _isStartNotified:${_isStartNotified} _isResultNotified:${_isResultNotified}")
        if (_isResultNotified) return false

        job.cancel()
        val isActive = job.isActive
        val isCancelled = !isActive

        if (isCancelled && !_isStartNotified) {
            HttpUtils.runOnUiThread {
                notifyCancel()
                notifyFinish()
            }
        }

        HttpLog.e("$_logPrefix cancel finish isActive:${isActive}")
        return isCancelled
    }

    private fun notifyStart() {
        HttpLog.i("$_logPrefix onStart ${Thread.currentThread()}")
        _isStartNotified = true
        _requestCallback.onStart()
    }

    private fun notifyError(e: Exception) {
        require(e !is CancellationException)
        HttpLog.i("$_logPrefix onError:$e ${Thread.currentThread()}")
        _isResultNotified = true
        _requestCallback.onError(HttpException.wrap(e))
    }

    private fun notifySuccess() {
        HttpLog.i("$_logPrefix onSuccess ${Thread.currentThread()}")
        _isResultNotified = true
        _requestCallback.onSuccess()
    }

    private fun notifyCancel() {
        HttpLog.i("$_logPrefix onCancel ${Thread.currentThread()}")
        _requestCallback.onCancel()
    }

    private fun notifyFinish() {
        HttpLog.i("$_logPrefix onFinish ${Thread.currentThread()}")
        _requestCallback.onFinish()
        this@RequestTask.onFinish()
    }

    abstract fun onFinish()

    override fun onProgressUpload(param: TransmitParam) {
        _requestCallback.onProgressUpload(param)
    }

    companion object {
        /** 单线程调度器 */
        @JvmStatic
        internal val singleThreadContext: ExecutorCoroutineDispatcher by lazy {
            newSingleThreadContext("FHttp single thread")
        }
    }
}
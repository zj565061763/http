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
    private var _isSuccessNotified: Boolean = false

    @Volatile
    private var _isErrorNotified: Boolean = false

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
     * 提交任务
     */
    fun submit() {
        submitInternal()
    }

    @Synchronized
    private fun submitInternal() {
        if (_job != null) return

        _job = GlobalScope.launch(Dispatchers.Main) {
            try {
                withContext(Dispatchers.Main) {
                    notifyStart()
                }

                val exception: Exception? = withContext(Dispatchers.IO) {
                    try {
                        HttpLog.i("$_logPrefix execute ${Thread.currentThread()}")
                        val response = _request.execute()

                        HttpLog.i("$_logPrefix onSuccessBackground ${Thread.currentThread()}")
                        _requestCallback.saveResponse(response)
                        _requestCallback.onSuccessBackground()
                        null
                    } catch (e: Exception) {
                        e
                    }
                }

                if (exception != null) {
                    withContext(Dispatchers.Main) {
                        notifyError(exception)
                    }
                    return@launch
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

        val isResultNotified = _isSuccessNotified || _isErrorNotified
        HttpLog.e("$_logPrefix cancel start isStartNotified:${_isStartNotified} isResultNotified:${isResultNotified}")
        if (isResultNotified) return false

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
        _isErrorNotified = true
        _requestCallback.onError(HttpException.wrap(e))
    }

    private fun notifySuccess() {
        HttpLog.i("$_logPrefix onSuccess ${Thread.currentThread()}")
        _isSuccessNotified = true
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
}
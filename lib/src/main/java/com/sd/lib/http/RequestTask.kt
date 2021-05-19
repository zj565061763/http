package com.sd.lib.http

import com.sd.lib.http.callback.IUploadProgressCallback
import com.sd.lib.http.callback.RequestCallback
import com.sd.lib.http.exception.HttpException
import com.sd.lib.http.utils.HttpLog
import com.sd.lib.http.utils.HttpUtils
import com.sd.lib.http.utils.TransmitParam
import kotlinx.coroutines.*

internal abstract class RequestTask {
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

        request.uploadCallback = object : IUploadProgressCallback {
            override fun onProgressUpload(params: TransmitParam) {
                _requestCallback.notifyProgressUpload(params)
            }
        }
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
                // 通知开始
                withContext(Dispatchers.Main) {
                    notifyStart()
                }

                // 发起请求
                val response: IResponse = withContext(Dispatchers.IO) {
                    try {
                        HttpLog.i("$_logPrefix execute ${Thread.currentThread().name}")
                        _request.execute()
                    } catch (e: Exception) {
                        notifyError(e)
                        null
                    }
                } ?: return@launch

                // 通知请求结果
                val notifyResponse: Boolean = withContext(Dispatchers.IO) {
                    try {
                        HttpLog.i("$_logPrefix notifyResponse ${Thread.currentThread().name}")
                        _requestCallback.notifyResponse(response)
                        true
                    } catch (e: Exception) {
                        notifyError(e)
                        false
                    }
                }

                if (notifyResponse) {
                    // 通知成功
                    notifySuccess()
                }

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

        val isResultNotified = _isResultNotified
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
        HttpLog.i("$_logPrefix notifyStart ${Thread.currentThread().name}")
        _isStartNotified = true
        _requestCallback.notifyStart()
    }

    private suspend fun notifyError(e: Exception) {
        require(e !is CancellationException)
        withContext(Dispatchers.Main) {
            HttpLog.i("$_logPrefix notifyError:$e ${Thread.currentThread().name}")
            _isResultNotified = true
            _requestCallback.notifyError(HttpException.wrap(e))
        }
    }

    private fun notifySuccess() {
        HttpLog.i("$_logPrefix notifySuccess ${Thread.currentThread().name}")
        _isResultNotified = true
        _requestCallback.notifySuccess()
    }

    private fun notifyCancel() {
        HttpLog.i("$_logPrefix notifyCancel ${Thread.currentThread().name}")
        _isResultNotified = true
        _requestCallback.notifyCancel()
    }

    private fun notifyFinish() {
        HttpLog.i("$_logPrefix notifyFinish ${Thread.currentThread().name}")
        _requestCallback.notifyFinish()
        this@RequestTask.onFinish()
    }

    abstract fun onFinish()
}
package com.sd.lib.http.callback

import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.RequestHandler
import com.sd.lib.http.exception.HttpExceptionResponseCode
import com.sd.lib.http.utils.HttpUtils
import com.sd.lib.http.utils.TransmitParam

abstract class RequestCallback {
    private lateinit var _request: IRequest
    private lateinit var _requestHandler: RequestHandler

    /**
     * [onPrepare]以及之后不为null
     */
    val httpRequest: IRequest
        get() = _request

    /**
     * [onPrepare]以及之后不为null，但是在[onPrepare]中由于请求还未被提交，所以对象的方法调用无效
     */
    val httpRequestHandler: RequestHandler
        get() = _requestHandler

    //---------- internal start ----------

    internal open fun saveRequest(request: IRequest) {
        _request = request
    }

    internal open fun saveRequestHandler(requestHandler: RequestHandler) {
        _requestHandler = requestHandler
    }

    internal open fun notifyPrepare() {
        onPrepare(httpRequest)
    }

    internal open fun notifyStart() {
        HttpUtils.checkMainThread()
        onStart()
    }

    internal open fun notifyResponse(response: IResponse) {
        HttpUtils.checkBackgroundThread()
        processResponseCode(response.code)
        onResponseBackground(response)
    }

    internal open fun notifySuccess() {
        HttpUtils.checkMainThread()
        onSuccessBefore()
        onSuccess()
    }

    internal open fun notifyError(e: Exception) {
        HttpUtils.checkMainThread()
        onError(e)
    }

    internal open fun notifyCancel() {
        HttpUtils.checkMainThread()
        onCancel()
    }

    internal open fun notifyFinish() {
        HttpUtils.checkMainThread()
        onFinish()
    }

    internal open fun notifyProgressUpload(params: TransmitParam) {
        HttpUtils.checkMainThread()
        onProgressUpload(params)
    }

    //---------- internal end ----------

    /**
     * 预备回调（和提交请求对象在同一个线程）
     */
    protected open fun onPrepare(request: IRequest) {}

    /**
     * 开始回调（UI线程）
     */
    protected open fun onStart() {}

    /**
     * 成功回调，常用来解析数据（后台线程）
     */
    @Throws(Exception::class)
    protected open fun onResponseBackground(response: IResponse) {
    }

    /**
     * 处理返回码（后台线程）
     */
    @Throws(HttpExceptionResponseCode::class)
    protected open fun processResponseCode(code: Int) {
        val codeException = HttpExceptionResponseCode.from(code)
        if (codeException != null) throw codeException
    }

    /**
     * 成功回调，在[onSuccess]之前执行，常用来做一些统一的处理（UI线程）
     */
    protected open fun onSuccessBefore() {}

    /**
     * 成功回调，在[onSuccessBefore]之后执行（UI线程）
     */
    protected abstract fun onSuccess()

    /**
     * 错误回调（UI线程）
     */
    protected open fun onError(e: Exception) {}

    /**
     * 取消回调（UI线程）
     */
    protected open fun onCancel() {}

    /**
     * 结束回调（UI线程）
     */
    protected open fun onFinish() {}

    /**
     * 上传回调（UI线程）
     */
    protected open fun onProgressUpload(params: TransmitParam) {}
}
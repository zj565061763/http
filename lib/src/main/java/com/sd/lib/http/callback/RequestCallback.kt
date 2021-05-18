package com.sd.lib.http.callback

import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.RequestHandler
import com.sd.lib.http.exception.HttpExceptionResponseCode
import com.sd.lib.http.utils.HttpUtils
import com.sd.lib.http.utils.TransmitParam

abstract class RequestCallback : IUploadProgressCallback {
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

    internal open fun saveResponse(response: IResponse) {
        HttpUtils.checkBackgroundThread()
        processResponseCode(response.code)
        onSuccessBackground(response)
    }

    //---------- internal end ----------

    //---------- notify method start ----------

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
    protected open fun onSuccessBackground(response: IResponse) {
        HttpUtils.checkBackgroundThread()
    }

    /**
     * 处理返回码
     */
    @Throws(HttpExceptionResponseCode::class)
    protected open fun processResponseCode(code: Int) {
        val codeException = HttpExceptionResponseCode.from(code)
        if (codeException != null) throw codeException
    }

    /**
     * 成功回调，常用来做一些统一的处理（UI线程）
     */
    open fun onSuccessBefore() {
        HttpUtils.checkMainThread()
    }

    /**
     * 成功回调，在[onSuccessBefore]方法之后执行（UI线程）
     */
    abstract fun onSuccess()

    /**
     * 上传回调（UI线程）
     */
    override fun onProgressUpload(param: TransmitParam) {
        HttpUtils.checkMainThread()
    }

    /**
     * 错误回调（UI线程）
     */
    open fun onError(e: Exception) {
        HttpUtils.checkMainThread()
    }

    /**
     * 取消回调（UI线程）
     */
    open fun onCancel() {
        HttpUtils.checkMainThread()
    }

    /**
     * 结束回调（UI线程）
     */
    open fun onFinish() {
        HttpUtils.checkMainThread()
    }

    //---------- notify method end ----------
}
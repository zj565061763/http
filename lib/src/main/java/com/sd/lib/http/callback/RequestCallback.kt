package com.sd.lib.http.callback

import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.RequestHandler
import com.sd.lib.http.exception.HttpExceptionResponseCode
import com.sd.lib.http.utils.HttpUtils
import com.sd.lib.http.utils.TransmitParam

abstract class RequestCallback : IUploadProgressCallback {
    private lateinit var _request: IRequest
    private lateinit var _response: IResponse
    private lateinit var _requestHandler: RequestHandler

    /**
     * 请求对象，[onPrepare]以及之后不为null
     */
    val httpRequest: IRequest
        get() = _request

    /**
     * 请求响应对象，onSuccessXXXX方法以及之后不为null
     */
    val httpResponse: IResponse
        get() = _response

    /**
     *  [onPrepare]以及之后不为null，但是在[onPrepare]中由于请求还未被提交，所以对象的方法调用无效
     */
    val httpRequestHandler: RequestHandler
        get() = _requestHandler

    internal open fun saveRequest(request: IRequest) {
        _request = request
    }

    internal open fun saveResponse(response: IResponse) {
        HttpUtils.checkBackgroundThread()
        _response = response
    }

    internal open fun saveRequestHandler(requestHandler: RequestHandler) {
        _requestHandler = requestHandler
    }

    //---------- notify method start ----------

    /**
     * 预备回调（和提交请求对象在同一个线程）
     */
    open fun onPrepare(request: IRequest) {}

    /**
     * 开始回调（UI线程）
     */
    open fun onStart() {}

    /**
     * 成功回调，常用来解析数据（后台线程）
     */
    @Throws(Exception::class)
    open fun onSuccessBackground() {
        HttpUtils.checkBackgroundThread()
        processResponseCode(httpResponse.code)
    }

    /**
     * 处理返回码
     */
    @Throws(Exception::class)
    protected fun processResponseCode(code: Int) {
        val codeException = HttpExceptionResponseCode.from(code)
        if (codeException != null) throw codeException
    }

    /**
     * 成功回调，常用来做一些统一的处理（UI线程）
     */
    open fun onSuccessBefore() {}

    /**
     * 成功回调，在[onSuccessBefore]方法之后执行（UI线程）
     */
    abstract fun onSuccess()

    /**
     * 上传回调（UI线程）
     */
    override fun onProgressUpload(param: TransmitParam) {}

    /**
     * 错误回调（UI线程）
     */
    open fun onError(e: Exception) {}

    /**
     * 取消回调（UI线程）
     */
    open fun onCancel() {}

    /**
     * 结束回调（UI线程）
     */
    open fun onFinish() {}

    //---------- notify method end ----------
}
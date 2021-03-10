package com.sd.lib.http.callback

import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.RequestHandler
import com.sd.lib.http.exception.HttpExceptionResponseCode
import com.sd.lib.http.utils.HttpUtils
import com.sd.lib.http.utils.TransmitParam

abstract class RequestCallback : IUploadProgressCallback {

    private var mRequest: IRequest? = null
    private var mResponse: IResponse? = null
    private var mRequestHandler: RequestHandler? = null

    /**
     * 请求对象，[onPrepare]以及之后不为null
     */
    val request: IRequest?
        get() = mRequest

    /**
     * 请求响应对象，onSuccessXXXX方法以及之后不为null
     */
    val response: IResponse?
        get() = mResponse

    /**
     *  [onPrepare]以及之后不为null，但是在[onPrepare]中由于请求还未被提交，所以对象的方法调用无效
     */
    val requestHandler: RequestHandler?
        get() = mRequestHandler

    internal open fun saveRequest(request: IRequest) {
        mRequest = request
    }

    internal open fun saveResponse(response: IResponse) {
        HttpUtils.checkBackgroundThread()
        mResponse = response
    }

    internal open fun saveRequestHandler(requestHandler: RequestHandler) {
        mRequestHandler = requestHandler
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
        processResponseCode(response!!.code)
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
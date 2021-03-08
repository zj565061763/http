package com.sd.lib.http.callback

import com.sd.lib.http.IRequest
import com.sd.lib.http.IRequestTaskApi
import com.sd.lib.http.IResponse
import com.sd.lib.http.exception.HttpExceptionResponseCode
import com.sd.lib.http.utils.HttpUtils
import com.sd.lib.http.utils.TransmitParam

abstract class RequestCallback : IUploadProgressCallback {

    private var mRequest: IRequest? = null
    private var mResponse: IResponse? = null
    private var mRequestTaskApi: IRequestTaskApi? = null

    /** 请求对象，任务被提交之后才可以访问 */
    val request: IRequest?
        get() = mRequest

    /** 请求返回对象，onSuccessXXXX方法中以及之后不为null */
    val response: IResponse?
        get() = mResponse

    /** 请求任务api接口，任务被提交之后才可以访问 */
    val requestTaskApi: IRequestTaskApi?
        get() = mRequestTaskApi

    /**
     * 保存请求对象
     */
    internal open fun saveRequest(request: IRequest) {
        mRequest = request
    }

    /**
     * 保存返回对象（后台线程）
     */
    internal open fun saveResponse(response: IResponse) {
        HttpUtils.checkBackgroundThread()
        mResponse = response
    }

    internal open fun saveRequestTaskApi(requestTaskApi: IRequestTaskApi) {
        mRequestTaskApi = requestTaskApi
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
     * 成功回调，常用来处理解析数据（后台线程）
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
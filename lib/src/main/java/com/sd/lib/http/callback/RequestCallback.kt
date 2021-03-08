package com.sd.lib.http.callback

import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.exception.HttpExceptionResponseCode.Companion.from
import com.sd.lib.http.utils.TransmitParam

abstract class RequestCallback : IUploadProgressCallback {
    open var request: IRequest? = null
    open var response: IResponse? = null

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
        processResponseCode(response!!.code)
    }

    /**
     * 处理返回码
     */
    @Throws(Exception::class)
    protected fun processResponseCode(code: Int) {
        val codeException = from(code)
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
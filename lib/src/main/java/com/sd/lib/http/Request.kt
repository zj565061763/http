package com.sd.lib.http

import com.sd.lib.http.callback.IUploadProgressCallback
import com.sd.lib.http.callback.RequestCallback
import com.sd.lib.http.exception.HttpException
import com.sd.lib.http.utils.HttpDataHolder
import com.sd.lib.http.utils.HttpUtils
import com.sd.lib.http.utils.TransmitParam
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory

abstract class Request : IRequest {

    private val uploadTransmitParam: TransmitParam by lazy { TransmitParam() }

    //---------- IRequest implements start ----------

    override var baseUrl: String = ""
        get() {
            if (field == null) field = ""
            return field
        }

    override var urlSuffix: String = ""
        get() {
            if (field == null) field = ""
            return field
        }

    override val url: String
        get() = baseUrl + urlSuffix

    override var tag: String? = null

    override var connectTimeout = IRequest.DEFAULT_CONNECT_TIMEOUT

    override var readTimeout = IRequest.DEFAULT_READ_TIMEOUT

    override val params by lazy { HttpDataHolder<String, Any>() }

    override val headers by lazy { HttpDataHolder<String, String>() }

    override var interceptExecute: Boolean = true

    override var uploadProgressCallback: IUploadProgressCallback? = null

    override var sSLSocketFactory: SSLSocketFactory? = null

    override var hostnameVerifier: HostnameVerifier? = null

    override fun execute(callback: RequestCallback?): RequestHandler {
        return RequestManager.getInstance().execute(this, callback)
    }

    override fun executeSequence(callback: RequestCallback?): RequestHandler {
        return RequestManager.getInstance().execute(this, true, callback)
    }

    @Throws(Exception::class)
    override fun execute(): IResponse {
        val intercept = interceptExecute
        if (intercept) {
            try {
                val beforeResponse = RequestManager.getInstance().mInternalRequestInterceptor.beforeExecute(this)
                if (beforeResponse != null) return beforeResponse
            } catch (e: Exception) {
                RequestManager.getInstance().mInternalRequestInterceptor.onError(e)
            }
        }

        val realResponse = doExecute()
        if (intercept) {
            try {
                val afterResponse = RequestManager.getInstance().mInternalRequestInterceptor.afterExecute(this, realResponse)
                if (afterResponse != null) return afterResponse
            } catch (e: Exception) {
                RequestManager.getInstance().mInternalRequestInterceptor.onError(e)
            }
        }
        return realResponse
    }

    //---------- IRequest implements end ----------

    /**
     * 发起请求
     */
    @Throws(HttpException::class)
    protected abstract fun doExecute(): IResponse

    protected fun notifyProgressUpload(uploaded: Long, total: Long) {
        val callback = uploadProgressCallback ?: return

        if (uploadTransmitParam.transmit(total, uploaded)) {
            val param = uploadTransmitParam.copy()
            HttpUtils.runOnUiThread {
                callback.onProgressUpload(param)
            }
        }
    }
}
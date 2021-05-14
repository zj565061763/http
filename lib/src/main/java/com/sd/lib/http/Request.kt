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

    override var urlSuffix: String = ""

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
        return RequestManager.instance.execute(this, false, callback)
    }

    override fun executeSequence(callback: RequestCallback?): RequestHandler {
        return RequestManager.instance.execute(this, true, callback)
    }

    @Throws(HttpException::class)
    override fun execute(): IResponse {
        val intercept = interceptExecute
        if (intercept) {
            try {
                val beforeResponse = RequestManager.instance.mInternalRequestInterceptor.beforeExecute(this)
                if (beforeResponse != null) return beforeResponse
            } catch (e: Exception) {
                RequestManager.instance.mInternalRequestInterceptor.onError(e)
            }
        }

        val realResponse = try {
            doExecute()
        } catch (e: Exception) {
            throw HttpException.wrap(e)
        }

        if (intercept) {
            try {
                val afterResponse = RequestManager.instance.mInternalRequestInterceptor.afterExecute(this, realResponse)
                if (afterResponse != null) return afterResponse
            } catch (e: Exception) {
                RequestManager.instance.mInternalRequestInterceptor.onError(e)
            }
        }
        return realResponse
    }

    //---------- IRequest implements end ----------

    /**
     * 发起请求
     */
    @Throws(Exception::class)
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
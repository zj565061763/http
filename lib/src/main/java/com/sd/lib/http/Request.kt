package com.sd.lib.http

import com.sd.lib.http.callback.IUploadProgressCallback
import com.sd.lib.http.callback.RequestCallback
import com.sd.lib.http.exception.*
import com.sd.lib.http.utils.HttpDataHolder
import com.sd.lib.http.utils.HttpUtils
import com.sd.lib.http.utils.TransmitParam
import com.sd.lib.result.FResult
import java.lang.reflect.Modifier
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory

abstract class Request : IRequest {
    private val _uploadTransmitParam by lazy { TransmitParam() }

    /** 是否正在解析中 */
    private var _isParsing: Boolean = false

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
                val beforeResponse = RequestManager.instance.internalRequestInterceptor.beforeExecute(this)
                if (beforeResponse != null) return beforeResponse
            } catch (e: Exception) {
                RequestManager.instance.internalRequestInterceptor.onError(e)
            }
        }

        val realResponse = try {
            doExecute().also {
                it.code
            }
        } catch (e: Exception) {
            throw HttpException.wrap(e)
        }

        if (intercept) {
            try {
                val afterResponse = RequestManager.instance.internalRequestInterceptor.afterExecute(this, realResponse)
                if (afterResponse != null) return afterResponse
            } catch (e: Exception) {
                RequestManager.instance.internalRequestInterceptor.onError(e)
            }
        }
        return realResponse
    }

    @Synchronized
    override fun <T> parse(clazz: Class<T>, checkCancel: (() -> Boolean)?): FResult<T> {
        if (_isParsing) throw RuntimeException("Parse is in progress")

        val result = try {
            _isParsing = true
            parseInternal(clazz, checkCancel)
        } finally {
            _isParsing = false
        }
        return result
    }

    private fun <T> parseInternal(clazz: Class<T>, checkCancel: (() -> Boolean)?): FResult<T> {
        require(!clazz.isInterface) { "clazz is interface ${clazz}" }
        require(!Modifier.isAbstract(clazz.modifiers)) { "clazz is abstract ${clazz}" }

        val result = try {
            val model = parseToModel(clazz, checkCancel)
            FResult.success(model)
        } catch (e: Exception) {
            val exception = HttpException.wrap(e)
            FResult.failure(exception)
        }

        if (result.failure is HttpExceptionCancellation) {
            // 如果是取消异常，则不经过拦截器，直接返回
            return result
        }

        val interceptor = RequestManager.instance.resultInterceptor
        if (interceptor != null) {
            val intercept = try {
                interceptor.intercept(this, result)
            } catch (e: Exception) {
                HttpUtils.runOnUiThread { throw e }
                false
            }

            if (intercept) {
                return FResult.failure(HttpExceptionResultIntercepted())
            }
        }

        return result
    }

    @Throws(Exception::class)
    private fun <T> parseToModel(clazz: Class<T>, checkCancel: (() -> Boolean)? = null): T {
        // 发起请求
        val response = execute()

        // 检查是否需要取消
        if (checkCancel != null && checkCancel()) {
            throw HttpExceptionCancellation()
        }

        // 检查返回码
        val exceptionCode = HttpExceptionResponseCode.from(response.code)
        if (exceptionCode != null) throw exceptionCode

        // 解析数据
        val parser = RequestManager.instance.responseParser
        val model = try {
            parser.parse(clazz, response) ?: throw HttpExceptionParseResponse(cause = null)
        } catch (e: Exception) {
            if (e is HttpException) {
                throw e
            } else {
                throw HttpExceptionParseResponse(cause = e)
            }
        }

        // 检查是否需要取消
        if (checkCancel != null && checkCancel()) {
            throw HttpExceptionCancellation()
        }

        return model
    }

    //---------- IRequest implements end ----------

    /**
     * 发起请求
     */
    @Throws(Exception::class)
    protected abstract fun doExecute(): IResponse

    protected fun notifyProgressUpload(uploaded: Long, total: Long) {
        if (_uploadTransmitParam.transmit(total, uploaded)) {
            val copyParam = _uploadTransmitParam.copy()
            HttpUtils.runOnUiThread {
                uploadProgressCallback?.onProgressUpload(copyParam)
            }
        }
    }

}
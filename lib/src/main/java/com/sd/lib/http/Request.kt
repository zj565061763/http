package com.sd.lib.http

import com.sd.lib.http.callback.IUploadCallback
import com.sd.lib.http.callback.RequestCallback
import com.sd.lib.http.exception.*
import com.sd.lib.http.parser.IResponseParser
import com.sd.lib.http.utils.HttpDataHolder
import com.sd.lib.http.utils.HttpUtils
import com.sd.lib.http.utils.TransmitParam
import com.sd.lib.result.FResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
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

    override var connectTimeout: Int = IRequest.DEFAULT_CONNECT_TIMEOUT

    override var readTimeout: Int = IRequest.DEFAULT_READ_TIMEOUT

    override val params by lazy { HttpDataHolder<String, Any>() }

    override val headers by lazy { HttpDataHolder<String, String>() }

    override var extra: Any? = null

    override var interceptExecute: Boolean = true

    override var interceptResult: Boolean = true

    override var responseParser: IResponseParser? = null

    override var uploadCallback: IUploadCallback? = null

    override var sSLSocketFactory: SSLSocketFactory? = null

    override var hostnameVerifier: HostnameVerifier? = null

    override fun execute(callback: RequestCallback?): RequestHandler {
        return RequestManager.execute(this, callback)
    }

    @Throws(HttpException::class)
    override fun execute(): IResponse {
        val intercept = interceptExecute
        if (intercept) {
            val response = RequestManager.internalRequestInterceptor.beforeExecute(this)
            if (response != null) return response
        }

        val realResponse = try {
            doExecute().also {
                it.code
            }
        } catch (e: Exception) {
            throw HttpException.wrap(e)
        }

        if (intercept) {
            val response = RequestManager.internalRequestInterceptor.afterExecute(this, realResponse)
            if (response != null) return response
        }
        return realResponse
    }

    @Synchronized
    override fun <T> parse(clazz: Class<T>, checkCancel: (() -> Boolean)?): FResult<T> {
        if (_isParsing) throw RuntimeException("Parse is in progress")

        return try {
            _isParsing = true
            parseInternal(clazz, checkCancel)
        } finally {
            _isParsing = false
        }
    }

    override suspend fun <T> parseSuspend(clazz: Class<T>): FResult<T> {
        return withContext(Dispatchers.IO) {
            parse(clazz) { !isActive }
        }
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

        if (result is FResult.Failure && result.exception is HttpExceptionCancellation) {
            // 如果是取消异常，则不经过拦截器，直接返回
            return result
        }

        val interceptor = RequestManager.resultInterceptor
        if (interceptor != null && interceptResult) {
            val intercept = try {
                interceptor.intercept(result, this)
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

        // 将内容解析为实体
        val parser = responseParser ?: RequestManager.responseParser
        val model = try {
            parser.parse(response, clazz, this)
                ?: throw HttpExceptionParseResponse(message = "parse return null")
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

    protected fun notifyUploadProgress(uploaded: Long, total: Long) {
        if (_uploadTransmitParam.transmit(total, uploaded)) {
            val copyParam = _uploadTransmitParam.copy()
            HttpUtils.runOnUiThread {
                uploadCallback?.onUploadProgress(copyParam)
            }
        }
    }
}
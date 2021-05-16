package com.sd.lib.http.impl

import com.sd.lib.http.IResponse
import com.sd.lib.http.Request
import com.sd.lib.http.exception.HttpException
import com.sd.lib.http.security.SSLSocketFactoryProvider
import com.sd.lib.http.utils.HttpIOUtils
import java.io.IOException
import java.io.InputStream
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory

abstract class BaseRequestImpl() : Request() {

    protected fun newHttpRequest(url: String, method: String): HttpRequest {
        val request = FHttpRequest(url, method)
        request.headers(headers.toMap())
        request.readTimeout(readTimeout)
        request.connectTimeout(connectTimeout)
        request.progress { uploaded, total -> notifyProgressUpload(uploaded, total) }

        val connection = request.connection
        if (connection is HttpsURLConnection) {
            val factory = sSLSocketFactory ?: trustedFactory
            connection.sslSocketFactory = factory

            val verifier = hostnameVerifier
            if (verifier != null) {
                connection.hostnameVerifier = verifier
            }
        }
        return request
    }

    override fun toString(): String {
        val url = HttpRequest.append(url, params.toMap())
        return url + " " + super.toString()
    }

    class Response : IResponse {
        private val _httpRequest: HttpRequest
        private var _content: String? = null

        constructor(httpRequest: HttpRequest) {
            _httpRequest = httpRequest
        }

        override val code: Int
            get() {
                try {
                    return _httpRequest.code()
                } catch (e: HttpRequest.HttpRequestException) {
                    throw e.cause ?: e
                }
            }

        override val contentLength: Int
            get() = _httpRequest.contentLength()

        override val headers: Map<String, List<String>>
            get() = _httpRequest.headers()

        override val charset: String?
            get() = _httpRequest.charset()

        override val inputStream: InputStream
            get() = _httpRequest.stream()

        override val isClosed: Boolean
            get() = try {
                inputStream.available() <= 0
            } catch (e: IOException) {
                true
            }

        @get:Throws(HttpException::class)
        override val asString: String
            get() {
                synchronized(this@Response) {
                    val content = _content
                    if (content != null) {
                        // 已经读取过了，直接返回保存的内容
                        return content
                    }

                    if (isClosed) {
                        // 输入流已经被关闭，返回空字符串
                        return ""
                    }

                    try {
                        _content = HttpIOUtils.readString(inputStream, charset)
                        return _content!!
                    } catch (e: IOException) {
                        throw HttpException.wrap(e)
                    } finally {
                        HttpIOUtils.closeQuietly(inputStream)
                    }
                }
            }
    }

    companion object {
        private val trustedFactory: SSLSocketFactory by lazy {
            SSLSocketFactoryProvider.getTrustedFactory()
        }
    }
}
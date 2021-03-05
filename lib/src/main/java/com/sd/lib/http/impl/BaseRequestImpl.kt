package com.sd.lib.http.impl

import android.text.TextUtils
import com.sd.lib.http.IResponse
import com.sd.lib.http.Request
import com.sd.lib.http.exception.HttpException
import com.sd.lib.http.impl.HttpRequest.HttpRequestException
import com.sd.lib.http.security.SSLSocketFactoryProvider
import com.sd.lib.http.utils.HttpIOUtil
import java.io.IOException
import java.io.InputStream
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
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
            var factory = sSLSocketFactory
            if (factory == null) {
                try {
                    factory = trustedFactory
                } catch (e: Exception) {
                    throw HttpException(cause = e)
                }
            }
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

        private val mHttpRequest: HttpRequest

        constructor(httpRequest: HttpRequest) {
            mHttpRequest = httpRequest
        }

        private var mBody: String? = null

        override val code: Int
            get() = mHttpRequest.code()

        @get:Throws(HttpException::class)
        val codeOrThrow: Int
            get() {
                try {
                    return code
                } catch (e: HttpRequestException) {
                    throw HttpException(cause = e.cause)
                }
            }

        override val contentLength: Int
            get() = mHttpRequest.contentLength()

        override val headers: Map<String, List<String>>
            get() = mHttpRequest.headers()

        override val charset: String?
            get() = mHttpRequest.charset()

        override val inputStream: InputStream
            get() = mHttpRequest.stream()

        @get:Throws(HttpException::class)
        override val asString: String
            get() {
                synchronized(this@Response) {
                    if (TextUtils.isEmpty(mBody)) {
                        try {
                            mBody = HttpIOUtil.readString(inputStream, charset)
                        } catch (e: IOException) {
                            throw HttpException(cause = e)
                        } finally {
                            HttpIOUtil.closeQuietly(inputStream)
                        }
                    }
                    return mBody!!
                }
            }
    }

    companion object {

        @get:Throws(NoSuchAlgorithmException::class, KeyManagementException::class)
        private val trustedFactory: SSLSocketFactory by lazy {
            SSLSocketFactoryProvider.getTrustedFactory()
        }
    }
}
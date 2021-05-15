package com.sd.lib.http.impl

import android.text.TextUtils
import com.sd.lib.http.RequestManager
import com.sd.lib.http.utils.HttpLog
import java.net.HttpCookie

internal class FHttpRequest : HttpRequest {
    constructor(url: CharSequence?, method: String?) : super(url, method) {
        loadCookieForRequest()
    }

    private var _code: Int? = null

    private val _responseCookie: List<HttpCookie>?
        private get() {
            val headers = headers()
            if (headers == null || headers.isEmpty()) return null

            val listCookie = headers[HEADER_SET_COOKIE]
            if (listCookie == null || listCookie.isEmpty()) return null

            val listResult = mutableListOf<HttpCookie>()
            for (item in listCookie) {
                listResult.addAll(HttpCookie.parse(item))
            }
            return listResult
        }

    /**
     * 填充cookie到请求对象
     */
    private fun loadCookieForRequest() {
        try {
            val cookieStore = RequestManager.instance.cookieStore
            val uri = url().toURI()
            val listCookie = cookieStore[uri]

            if (listCookie != null && !listCookie.isEmpty()) {
                val cookie = TextUtils.join(";", listCookie)
                header(HEADER_COOKIE, cookie)
                HttpLog.i(
                    """cookie loadCookieForRequest $uri
                    |$cookie
                """.trimMargin()
                )
            }
        } catch (e: Exception) {
            HttpLog.e("cookie loadCookieForRequest error:$e")
        }
    }

    /**
     * 保存接口返回的cookie
     */
    private fun saveCookieFromResponse() {
        try {
            val cookieStore = RequestManager.instance.cookieStore
            val uri = url().toURI()
            val listCookie = _responseCookie

            if (listCookie != null) {
                cookieStore.add(uri, listCookie)

                HttpLog.i(
                    """cookie saveCookieFromResponse $uri
                |${TextUtils.join("\r\n", listCookie)}
            """.trimMargin()
                )
            }
        } catch (e: Exception) {
            HttpLog.e("cookie saveCookieFromResponse error:$e")
        }
    }

    //---------- Override start ----------

    @Throws(HttpRequestException::class)
    override fun code(): Int {
        val code = super.code()
        if (_code == null) {
            _code = code
            saveCookieFromResponse()
        }
        return code
    }

    //---------- Override end ----------

    companion object {
        /** 'Set-Cookie' header name */
        const val HEADER_SET_COOKIE = "Set-Cookie"

        /** 'Cookie' header name */
        const val HEADER_COOKIE = "Cookie"
    }
}
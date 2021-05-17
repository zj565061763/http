package com.sd.lib.http.impl

import com.sd.lib.http.RequestManager
import com.sd.lib.http.utils.HttpLog
import java.net.HttpCookie

internal class FHttpRequest : HttpRequest {
    private var _code: Int? = null

    constructor(url: CharSequence, method: String) : super(url, method) {
        loadCookieForRequest()
    }

    /**
     * 填充cookie到请求对象
     */
    private fun loadCookieForRequest() {
        try {
            val uri = url().toURI()
            val listCookie = RequestManager.instance.cookieStore.get(uri)
            if (listCookie == null || listCookie.isEmpty()) return

            val cookie = listCookie.joinToString(separator = ";")
            header(HEADER_COOKIE, cookie)

            HttpLog.i(
                """cookie loadCookieForRequest ${uri} size:${uri}${listCookie.size}
                ${cookie}
            """.trimIndent()
            )
        } catch (e: Exception) {
            HttpLog.e("cookie loadCookieForRequest error:$e")
        }
    }

    /**
     * 保存接口返回的cookie
     */
    private fun saveCookieFromResponse() {
        try {
            val listCookie = getResponseCookie()
            if (listCookie == null || listCookie.isEmpty()) return

            val uri = url().toURI()
            RequestManager.instance.cookieStore.add(uri, listCookie)

            HttpLog.i(
                """cookie saveCookieFromResponse ${uri} size:${listCookie.size}
                    ${listCookie}
            """.trimIndent()
            )
        } catch (e: Exception) {
            HttpLog.e("cookie saveCookieFromResponse error:${e}")
        }
    }

    /**
     * 获取接口返回的cookie
     */
    private fun getResponseCookie(): List<HttpCookie>? {
        val headers = headers()
        if (headers == null || headers.isEmpty()) return null

        var listCookie = headers[HEADER_SET_COOKIE]
        if (listCookie == null || listCookie.isEmpty()) listCookie = headers[HEADER_SET_COOKIE2]
        if (listCookie == null || listCookie.isEmpty()) return null

        val listResult = mutableListOf<HttpCookie>()
        for (item in listCookie) {
            val listItem = HttpCookie.parse(item)
            if (listItem != null) {
                listResult.addAll(listItem)
            }
        }
        return listResult
    }

    //---------- Override start ----------

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

        /** 'Set-Cookie2' header name */
        const val HEADER_SET_COOKIE2 = "Set-Cookie2"

        /** 'Cookie' header name */
        const val HEADER_COOKIE = "Cookie"
    }
}
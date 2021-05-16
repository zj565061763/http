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
                """
                cookie loadCookieForRequest ${uri}
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
                """
                cookie saveCookieFromResponse ${uri}
                ${listCookie.joinToString("\r\n")}
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

        val listCookie = headers[HEADER_SET_COOKIE]
        if (listCookie == null || listCookie.isEmpty()) return null

        val listResult = mutableListOf<HttpCookie>()
        for (item in listCookie) {
            listResult.addAll(HttpCookie.parse(item))
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

        /** 'Cookie' header name */
        const val HEADER_COOKIE = "Cookie"
    }
}
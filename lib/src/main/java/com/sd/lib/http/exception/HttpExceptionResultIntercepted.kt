package com.sd.lib.http.exception

import android.content.Context

/**
 * http请求结果被拦截
 */
class HttpExceptionResultIntercepted : HttpException() {
    override fun getDescFormat(context: Context): String {
        return ""
    }
}
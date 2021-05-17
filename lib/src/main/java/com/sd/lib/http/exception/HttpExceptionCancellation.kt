package com.sd.lib.http.exception

import android.content.Context

/**
 * http请求被取消
 */
class HttpExceptionCancellation : HttpException() {
    override fun getDescFormat(context: Context): String {
        return ""
    }
}
package com.sd.lib.http.exception

import android.content.Context
import com.sd.lib.http.R
import com.sd.lib.result.exception.FExceptionHttp

open class HttpException : FExceptionHttp {
    @JvmOverloads
    constructor(message: String? = "", cause: Throwable? = null) : super(message, cause)

    /**
     * 返回格式化好的描述文字
     */
    open fun getDescFormat(context: Context): String {
        return context.getString(R.string.lib_http_desc_exception_http)
    }

    companion object {
        @JvmStatic
        fun wrap(e: Exception): HttpException {
            return if (e is HttpException) e else HttpException(null, e)
        }
    }
}
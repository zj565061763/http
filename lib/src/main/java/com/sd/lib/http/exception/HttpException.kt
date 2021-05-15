package com.sd.lib.http.exception

import com.sd.lib.result.exception.FExceptionHttp

open class HttpException : FExceptionHttp {
    @JvmOverloads
    constructor(message: String? = "", cause: Throwable? = null) : super(message, cause)

    companion object {
        @JvmStatic
        fun wrap(e: Exception): HttpException {
            return if (e is HttpException) e else HttpException(null, e)
        }
    }
}
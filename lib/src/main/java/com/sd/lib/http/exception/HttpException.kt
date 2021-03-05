package com.sd.lib.http.exception

open class HttpException : Exception {
    @JvmOverloads
    constructor(message: String?, cause: Throwable? = null) : super(message, cause)
}
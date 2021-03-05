package com.sd.lib.http.exception

open class HttpException : Exception {
    @JvmOverloads
    constructor(message: String? = null, cause: Throwable? = null) : super(message, cause)
}
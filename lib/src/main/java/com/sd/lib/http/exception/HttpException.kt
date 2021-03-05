package com.sd.lib.http.exception

open class HttpException : Exception {
    constructor() {}
    constructor(cause: Throwable?) : super(cause) {}
}
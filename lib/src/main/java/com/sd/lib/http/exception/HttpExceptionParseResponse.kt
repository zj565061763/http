package com.sd.lib.http.exception

/**
 * Http返回解析异常
 */
class HttpExceptionParseResponse : HttpException {
    @JvmOverloads
    constructor(message: String? = "", cause: Throwable?) : super(message, cause)
}
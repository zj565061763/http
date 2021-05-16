package com.sd.lib.http.exception

/**
 * http返回数据解析异常
 */
class HttpExceptionParseResponse : HttpException {
    @JvmOverloads
    constructor(message: String? = "", cause: Throwable?) : super(message, cause)
}
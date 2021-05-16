package com.sd.lib.http.exception

/**
 * http返回码异常
 */
class HttpExceptionResponseCode : HttpException {
    val code: Int

    @JvmOverloads
    constructor(code: Int, message: String? = "") : super(message = message) {
        this.code = code
    }

    override fun toString(): String {
        return "${super.toString()} code:${code}"
    }

    companion object {
        @JvmStatic
        fun from(code: Int): HttpExceptionResponseCode? {
            return if (code >= 400) {
                HttpExceptionResponseCode(code)
            } else {
                null
            }
        }
    }
}
package com.sd.lib.http.exception

/**
 * Http返回码异常
 */
class HttpExceptionResponseCode @JvmOverloads constructor(val code: Int, message: String? = null, cause: Throwable? = null) : HttpException(message, cause) {

    override fun toString(): String {
        return "${super.toString()} code:${this.code}"
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
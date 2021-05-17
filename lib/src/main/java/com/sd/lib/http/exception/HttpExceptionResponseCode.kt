package com.sd.lib.http.exception

import android.content.Context
import com.sd.lib.http.R

/**
 * http返回码异常
 */
class HttpExceptionResponseCode : HttpException {
    val code: Int

    @JvmOverloads
    constructor(code: Int, message: String? = "") : super(message = message) {
        this.code = code
    }

    override fun getDescFormat(context: Context): String {
        var desc = context.getString(R.string.lib_http_desc_exception_response_code)
        return desc + toStringSuffix()
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
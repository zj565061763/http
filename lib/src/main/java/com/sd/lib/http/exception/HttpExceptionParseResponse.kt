package com.sd.lib.http.exception

import android.content.Context
import com.sd.lib.http.R

/**
 * http返回数据解析异常
 */
class HttpExceptionParseResponse : HttpException {
    @JvmOverloads
    constructor(message: String? = "", cause: Throwable?) : super(message, cause)

    override fun getDescFormat(context: Context): String {
        return context.getString(R.string.lib_http_desc_exception_parse_response, toString())
    }
}
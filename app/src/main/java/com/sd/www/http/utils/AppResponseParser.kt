package com.sd.www.http.utils

import com.sd.lib.http.IRequest
import com.sd.lib.http.parser.DefaultResponseParser

class AppResponseParser : DefaultResponseParser() {
    override fun <T> parse(content: String, clazz: Class<T>, request: IRequest): T {
        return super.parse(content, clazz, request)
    }
}
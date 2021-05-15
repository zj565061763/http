package com.sd.www.http.utils

import com.sd.lib.http.IResponse
import com.sd.lib.http.parser.DefaultResponseParser

class AppResponseParser : DefaultResponseParser() {
    override fun <T> parseContent(clazz: Class<T>, response: IResponse, content: String): T {
        return super.parseContent(clazz, response, content)
    }
}
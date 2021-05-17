package com.sd.lib.http.parser

import com.google.gson.Gson
import com.sd.lib.http.IRequest

/**
 * 默认http返回解析器
 */
open class DefaultResponseParser : IResponseParser {
    override fun <T> parse(content: String, clazz: Class<T>, request: IRequest): T {
        return Gson().fromJson(content, clazz)
    }
}
package com.sd.lib.http.parser

import com.sd.lib.http.IRequest
import com.sd.lib.utils.json.FJson

/**
 * 默认http返回解析器
 */
open class DefaultResponseParser : IResponseParser {
    override fun <T> parse(content: String, clazz: Class<T>, request: IRequest): T {
        return FJson.GSON.fromJson(content, clazz)
    }
}
package com.sd.lib.http.parser

import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.utils.json.FJson

/**
 * 默认http返回解析器
 */
open class DefaultResponseParser : IResponseParser {
    override fun <T> parse(response: IResponse, clazz: Class<T>, request: IRequest): T {
        val content = response.readString()
        if (clazz == String::class.java) {
            return content as T
        }
        return FJson.GSON.fromJson(content, clazz)
    }
}
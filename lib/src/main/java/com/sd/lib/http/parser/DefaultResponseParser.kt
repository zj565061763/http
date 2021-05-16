package com.sd.lib.http.parser

import com.google.gson.Gson
import com.sd.lib.http.IResponse

/**
 * 默认http返回解析器
 */
open class DefaultResponseParser : IResponseParser {
    final override fun <T> parse(clazz: Class<T>, response: IResponse): T {
        val content = response.readString()
        if (clazz == String::class.java) {
            return content as T
        }
        return parseContent(clazz, response, content)
    }

    /**
     * 解析字符串内容
     */
    protected open fun <T> parseContent(clazz: Class<T>, response: IResponse, content: String): T {
        return Gson().fromJson(content, clazz)
    }
}
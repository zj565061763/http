package com.sd.lib.http.parser

import com.sd.lib.http.IResponse

/**
 * http返回解析器
 */
interface IResponseParser {
    @Throws(Exception::class)
    fun <T> parse(clazz: Class<T>, response: IResponse): T
}
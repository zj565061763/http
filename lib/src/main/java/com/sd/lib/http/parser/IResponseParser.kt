package com.sd.lib.http.parser

import com.sd.lib.http.IResponse

/**
 * 返回结果解析
 */
interface IResponseParser {
    @Throws(Exception::class)
    fun <T> parse(clazz: Class<T>, response: IResponse): T
}
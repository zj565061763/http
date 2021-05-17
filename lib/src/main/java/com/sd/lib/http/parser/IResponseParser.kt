package com.sd.lib.http.parser

import com.sd.lib.http.IRequest

/**
 * http返回解析器
 */
interface IResponseParser {
    /**
     * 解析（非UI线程）
     */
    @Throws(Exception::class)
    fun <T> parse(content: String, clazz: Class<T>, request: IRequest): T
}
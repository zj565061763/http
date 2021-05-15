package com.sd.lib.http.parser

import com.sd.lib.http.IResponse

/**
 * http返回解析器
 */
interface IResponseParser {
    /**
     * 解析（非UI线程）
     */
    @Throws(Exception::class)
    fun <T> parse(clazz: Class<T>, response: IResponse): T
}
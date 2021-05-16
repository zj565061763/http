package com.sd.lib.http

import com.sd.lib.http.exception.HttpException
import java.io.InputStream

interface IResponse {
    /**
     * http返回码
     */
    val code: Int

    /**
     * 内容长度
     */
    val contentLength: Int

    /**
     * 头信息
     */
    val headers: Map<String, List<String>>

    /**
     * 编码格式
     */
    val charset: String?

    /**
     * 输入流
     */
    val inputStream: InputStream

    /**
     * [readString]读取过之后，缓存的字符串内容。不为null，说明已经读取过了
     */
    val cachedString: String?

    /**
     * 读取输入流的内容，并以字符串返回，如果已经读取过了，则直接返回读取过的内容；
     * 如果未读取过，则会从输入流中读取，这时候需要在非UI线程操作
     */
    @Throws(HttpException::class)
    fun readString(): String

    /**
     * 用[readString]替代
     */
    @Deprecated("")
    @get:Throws(HttpException::class)
    val asString: String
}
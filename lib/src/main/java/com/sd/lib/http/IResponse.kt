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
     * 读取输入流的内容，并以字符串返回（必须在非UI线程调用）
     */
    @get:Throws(HttpException::class)
    val asString: String?
}
package com.sd.lib.http.target

import com.sd.lib.http.exception.HttpException

interface IHttpFuture<T> {
    /**
     * 目标对象，请求成功的时候不为null
     */
    val target: T?

    /**
     * 异常对象，请求出错的时候不为null
     */
    val exception: HttpException?

    /**
     * 获取请求结果，如果已经请求过了，则直接返回结果
     */
    suspend fun execute(): Result

    enum class Result {
        /** 成功 */
        Success,

        /** 异常 */
        Error,

        /** 取消 */
        Cancel
    }
}
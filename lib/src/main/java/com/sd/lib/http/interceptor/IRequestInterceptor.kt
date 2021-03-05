package com.sd.lib.http.interceptor

import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse

/**
 * 用于请求拦截
 */
interface IRequestInterceptor {
    /**
     * 发起网络请求之前回调
     *
     * @param request
     * @return 如果返回不为null，则使用该返回值
     */
    @Throws(Exception::class)
    fun beforeExecute(request: IRequest): IResponse?

    /**
     * 发起网络请求之后回调
     *
     * @param request
     * @param response 原始的返回
     * @return 如果返回不为null，则使用该返回值
     */
    @Throws(Exception::class)
    fun afterExecute(request: IRequest, response: IResponse): IResponse?

    /**
     * 异常回调
     */
    fun onError(e: Exception)
}
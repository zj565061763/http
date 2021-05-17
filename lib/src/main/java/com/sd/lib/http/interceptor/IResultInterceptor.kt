package com.sd.lib.http.interceptor

import com.sd.lib.http.IRequest
import com.sd.lib.http.exception.HttpExceptionResultIntercepted
import com.sd.lib.result.FResult

/**
 * http结果拦截器
 */
interface IResultInterceptor {
    /**
     * 返回true拦截，false不拦截。拦截之后，调用方会收到[HttpExceptionResultIntercepted]的失败结果（非UI线程）
     */
    fun intercept(result: FResult<*>, request: IRequest): Boolean
}
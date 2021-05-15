package com.sd.lib.http.interceptor

import com.sd.lib.http.exception.HttpExceptionParseIntercepted
import com.sd.lib.result.FResult

/**
 * 解析拦截器
 */
interface IParseInterceptor {
    /**
     * 返回true拦截，false不拦截。拦截之后，调用方会收到[HttpExceptionParseIntercepted]的失败结果（非UI线程）
     */
    fun intercept(result: FResult<*>): Boolean
}
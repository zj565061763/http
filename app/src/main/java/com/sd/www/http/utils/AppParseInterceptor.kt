package com.sd.www.http.utils

import com.sd.lib.http.interceptor.IParseInterceptor
import com.sd.lib.result.FResult
import com.sd.lib.utils.context.FToast

class AppParseInterceptor : IParseInterceptor {
    override fun intercept(result: FResult<*>): Boolean {
        if (result.isFailure) {
            FToast.show("解析拦截器：${result.failure}")
            // return true
        }

        // 返回true拦截，false不拦截
        return false
    }
}
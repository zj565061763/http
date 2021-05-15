package com.sd.www.http.utils

import com.sd.lib.http.interceptor.IResultInterceptor
import com.sd.lib.result.FResult
import com.sd.lib.utils.context.FToast

class AppResultInterceptor : IResultInterceptor {
    override fun intercept(result: FResult<*>): Boolean {
        if (result.isFailure) {
            FToast.show("结果拦截器：${result.failure}")
            // return true
        }

        // 返回true拦截，false不拦截
        return false
    }
}
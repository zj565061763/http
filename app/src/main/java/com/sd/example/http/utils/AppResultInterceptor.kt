package com.sd.example.http.utils

import android.util.Log
import com.sd.lib.http.IRequest
import com.sd.lib.http.interceptor.IResultInterceptor
import com.sd.lib.result.FResult
import com.sd.lib.utils.context.FToast

class AppResultInterceptor : IResultInterceptor {
    val TAG = AppResultInterceptor::class.java.simpleName

    override fun intercept(result: FResult<*>, request: IRequest): Boolean {
        Log.i(TAG, "intercept request:${request} extra:${request.extra}")

        if (result.isFailure) {
            FToast.show("结果拦截器：${result.exception}")
            // return true
        }

        // 返回true拦截，false不拦截
        return false
    }
}
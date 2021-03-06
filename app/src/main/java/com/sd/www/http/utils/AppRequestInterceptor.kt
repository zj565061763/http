package com.sd.www.http.utils

import android.util.Log
import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.interceptor.IRequestInterceptor
import java.util.*

/**
 * http请求拦截
 */
class AppRequestInterceptor : IRequestInterceptor {
    private val TAG = AppRequestInterceptor::class.java.simpleName
    private val mMapTime: MutableMap<IRequest, Long> = WeakHashMap()

    @Throws(Exception::class)
    override fun beforeExecute(request: IRequest): IResponse? {
        //请求发起之前回调
        mMapTime[request] = System.currentTimeMillis()
        Log.i(TAG, "beforeExecute:$request")
        return null
    }

    @Throws(Exception::class)
    override fun afterExecute(request: IRequest, response: IResponse): IResponse? {
        //请求发起之后回调
        val time = System.currentTimeMillis() - mMapTime.remove(request)!!
        Log.i(TAG, "afterExecute:$request $time")
        return null
    }

    override fun onError(e: Exception) {}
}
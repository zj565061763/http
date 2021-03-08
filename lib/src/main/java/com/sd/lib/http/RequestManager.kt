package com.sd.lib.http

import android.text.TextUtils
import android.util.Log
import com.sd.lib.http.callback.RequestCallback
import com.sd.lib.http.cookie.ICookieStore
import com.sd.lib.http.cookie.ModifyMemoryCookieStore
import com.sd.lib.http.interceptor.IRequestInterceptor
import com.sd.lib.http.utils.HttpUtils.Companion.runOnUiThread
import java.util.concurrent.ConcurrentHashMap

class RequestManager private constructor() {
    private val mMapRequest: MutableMap<RequestTask, RequestInfo> = ConcurrentHashMap()

    /** 调试模式 */
    var isDebug = false

    /** cookie管理对象 */
    var cookieStore: ICookieStore = ModifyMemoryCookieStore()
        set(value) {
            requireNotNull(value)
            field = value
        }

    /** 请求拦截 */
    var requestInterceptor: IRequestInterceptor? = null

    /** Request对象标识生成器 */
    private var requestIdentifierProvider: IRequestIdentifierProvider? = null

    //---------- IRequestInterceptor start ----------

    internal val mInternalRequestInterceptor = object : IRequestInterceptor {
        @Throws(Exception::class)
        override fun beforeExecute(request: IRequest): IResponse? {
            val interceptor = requestInterceptor ?: return null
            return interceptor.beforeExecute(request)
        }

        @Throws(Exception::class)
        override fun afterExecute(request: IRequest, response: IResponse): IResponse? {
            val interceptor = requestInterceptor ?: return response
            return interceptor.afterExecute(request, response)
        }

        override fun onError(e: Exception) {
            val interceptor = requestInterceptor
            if (interceptor != null) {
                interceptor.onError(e)
            } else {
                runOnUiThread { throw RuntimeException(e) }
            }
        }
    }

    //---------- IRequestInterceptor end ----------

    /**
     * 异步执行请求
     *
     * @param sequence 是否按顺序执行
     */
    @JvmOverloads
    @Synchronized
    fun execute(request: IRequest, sequence: Boolean = false, callback: RequestCallback?): RequestHandler {
        var callback = callback
        if (callback == null) {
            callback = object : RequestCallback() {
                override fun onSuccess() {}
            }
        }

        callback.request = request
        callback.onPrepare(request)

        val task = object : RequestTask(request, callback) {
            override fun onFinish() {
                removeTask(this)
            }
        }

        val info = RequestInfo().apply {
            this.tag = request.tag
            this.requestIdentifier = requestIdentifierProvider?.provideRequestIdentifier(request)
        }

        mMapRequest[task] = info
        if (isDebug) {
            Log.i(RequestManager::class.java.name, "execute"
                    + " task:${task}"
                    + " callback:${callback}"
                    + " requestIdentifier:${info.requestIdentifier}"
                    + " tag:${info.tag}"
                    + " size:${mMapRequest.size}")
        }

        if (sequence) {
            task.submitSequence()
        } else {
            task.submit()
        }
        return RequestHandler(task)
    }

    @Synchronized
    private fun removeTask(task: RequestTask): Boolean {
        val info = mMapRequest.remove(task) ?: return false
        if (isDebug) {
            Log.i(RequestManager::class.java.name, "removeTask"
                    + " task:" + task
                    + " size:" + mMapRequest.size)
        }
        return true
    }

    /**
     * 根据tag取消请求
     *
     * @param tag
     * @return 申请取消成功的数量
     */
    @Synchronized
    fun cancelTag(tag: String?): Int {
        if (tag == null || mMapRequest.isEmpty()) return 0

        if (isDebug) {
            Log.i(RequestManager::class.java.name, "try cancelTag tag:$tag")
        }

        var count = 0
        for ((task, info) in mMapRequest) {
            if (tag == info.tag && task.cancel()) {
                count++
            }
        }

        if (isDebug) {
            Log.i(RequestManager::class.java.name, "try cancelTag tag:$tag count:$count")
        }
        return count
    }

    /**
     * 根据Request的唯一标识取消请求[IRequestIdentifierProvider]
     *
     * @param request
     * @return
     */
    @Synchronized
    fun cancelRequestIdentifier(request: IRequest?): Int {
        if (request == null || mMapRequest.isEmpty()) return 0
        val identifier = requestIdentifierProvider?.provideRequestIdentifier(request)
        if (TextUtils.isEmpty(identifier)) {
            return 0
        }

        if (isDebug) {
            Log.i(RequestManager::class.java.name, "try cancelRequestIdentifier requestIdentifier:$identifier")
        }

        var count = 0
        for ((task, info) in mMapRequest) {
            if (identifier == info.requestIdentifier && task.cancel()) {
                count++
            }
        }

        if (isDebug) {
            Log.i(RequestManager::class.java.name, "try cancelRequestIdentifier requestIdentifier:$identifier count:$count")
        }
        return count
    }

    private class RequestInfo {
        var tag: String? = null
        var requestIdentifier: String? = null
    }

    companion object {
        val instance: RequestManager by lazy { RequestManager() }
    }
}
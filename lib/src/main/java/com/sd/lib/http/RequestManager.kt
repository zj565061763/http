package com.sd.lib.http

import android.util.Log
import com.sd.lib.http.callback.RequestCallback
import com.sd.lib.http.cookie.store.ICookieStore
import com.sd.lib.http.cookie.store.MemoryCookieStore
import com.sd.lib.http.interceptor.IRequestInterceptor
import com.sd.lib.http.interceptor.IResultInterceptor
import com.sd.lib.http.parser.DefaultResponseParser
import com.sd.lib.http.parser.IResponseParser
import com.sd.lib.http.utils.HttpUtils
import java.util.concurrent.ConcurrentHashMap

object RequestManager {
    @Deprecated("")
    @JvmStatic
    val instance: RequestManager by lazy { RequestManager }

    private val _mapRequest: MutableMap<RequestTask, RequestInfo> = ConcurrentHashMap()

    /** 调试模式 */
    var isDebug = false

    /** cookie管理对象 */
    var cookieStore: ICookieStore = MemoryCookieStore()

    /** 请求拦截 */
    var requestInterceptor: IRequestInterceptor? = null

    /** http返回解析器 */
    var responseParser: IResponseParser = DefaultResponseParser()

    /** 结果拦截器 */
    var resultInterceptor: IResultInterceptor? = null

    //---------- IRequestInterceptor start ----------

    internal val internalRequestInterceptor = object : IRequestInterceptor {
        override fun beforeExecute(request: IRequest): IResponse? {
            return try {
                requestInterceptor?.beforeExecute(request)
            } catch (e: Exception) {
                onError(e)
                null
            }
        }

        override fun afterExecute(request: IRequest, response: IResponse): IResponse? {
            return try {
                requestInterceptor?.afterExecute(request, response)
            } catch (e: Exception) {
                onError(e)
                null
            }
        }

        override fun onError(e: Exception) {
            val interceptor = requestInterceptor
            if (interceptor == null) {
                HttpUtils.runOnUiThread { throw RuntimeException(e) }
                return
            }

            try {
                interceptor.onError(e)
            } catch (newE: Exception) {
                HttpUtils.runOnUiThread { throw RuntimeException(newE) }
            }
        }
    }

    //---------- IRequestInterceptor end ----------

    /**
     * 异步执行请求
     */
    @Synchronized
    fun execute(request: IRequest, callback: RequestCallback?): RequestHandler {
        val finalCallback = callback ?: object : RequestCallback() {
            override fun onSuccess() {}
        }

        // 创建请求任务
        val task = object : RequestTask(request, finalCallback) {
            override fun onFinish() {
                removeTask(this)
            }
        }
        val requestHandler = RequestHandler(task)

        finalCallback.saveRequest(request)
        finalCallback.saveRequestHandler(requestHandler)
        finalCallback.notifyPrepare()

        val info = RequestInfo(request.tag)
        _mapRequest[task] = info

        if (isDebug) {
            Log.i(
                RequestManager::class.java.name, "execute"
                        + " task:${task}"
                        + " callback:${finalCallback}"
                        + " tag:${info.tag}"
                        + " size:${_mapRequest.size}"
            )
        }

        task.submit()
        return requestHandler
    }

    @Synchronized
    private fun removeTask(task: RequestTask): Boolean {
        _mapRequest.remove(task) ?: return false
        if (isDebug) {
            Log.i(
                RequestManager::class.java.name, "removeTask"
                        + " task:" + task
                        + " size:" + _mapRequest.size
            )
        }
        return true
    }

    /**
     * 根据[tag]取消请求
     *
     * @return 取消的任务数量
     */
    @Synchronized
    fun cancelTag(tag: String?): Int {
        if (tag == null || _mapRequest.isEmpty()) return 0

        if (isDebug) {
            Log.i(RequestManager::class.java.name, "try cancelTag tag:$tag")
        }

        var count = 0
        for ((task, info) in _mapRequest) {
            if (tag == info.tag && task.cancel()) {
                count++
            }
        }

        if (isDebug) {
            Log.i(RequestManager::class.java.name, "try cancelTag tag:$tag count:$count")
        }
        return count
    }

    private class RequestInfo(val tag: String?)
}
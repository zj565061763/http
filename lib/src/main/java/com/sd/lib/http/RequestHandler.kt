package com.sd.lib.http

/**
 * 执行异步请求后返回的对象，提供一些方法暴露给外部
 */
class RequestHandler internal constructor(task: RequestTask) {
    private val requestTask: RequestTask = task

    /**
     * 取消请求
     */
    fun cancel() {
        requestTask.cancel()
    }
}
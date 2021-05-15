package com.sd.lib.http

/**
 * 异步请求任务处理对象，提供一些方法暴露给外部
 */
class RequestHandler internal constructor(task: RequestTask) {
    private val _requestTask: RequestTask = task

    /**
     * 任务是否处于活动状态
     */
    val isActive: Boolean
        get() = _requestTask.isActive

    /**
     * 取消请求
     */
    fun cancel() {
        _requestTask.cancel()
    }
}
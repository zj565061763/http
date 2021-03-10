package com.sd.lib.http

/**
 * 异步请求任务处理对象，提供一些方法暴露给外部
 */
class RequestHandler internal constructor(task: RequestTask) {
    private val requestTask: RequestTask = task

    /**
     * 任务是否处于活动状态
     */
    val isActive: Boolean
        get() = requestTask.isActive

    /**
     * 取消请求
     */
    fun cancel() {
        requestTask.cancel()
    }
}
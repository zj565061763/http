package com.sd.lib.http

/**
 * 请求任务api接口
 */
interface IRequestTaskApi {
    /**
     * 任务是否处于活动状态
     */
    val isActive: Boolean
}
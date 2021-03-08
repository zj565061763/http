package com.sd.lib.http

/**
 * 请求任务api接口
 */
interface IRequestTaskApi {
    /**
     * 任务是否还存活
     */
    val isActive: Boolean
}
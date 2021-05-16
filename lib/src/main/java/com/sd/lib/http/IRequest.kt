package com.sd.lib.http

import com.sd.lib.http.callback.IUploadProgressCallback
import com.sd.lib.http.callback.RequestCallback
import com.sd.lib.http.exception.HttpException
import com.sd.lib.http.interceptor.IResultInterceptor
import com.sd.lib.http.utils.HttpDataHolder
import com.sd.lib.result.FResult
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory

interface IRequest {
    /**
     * 设置基础url
     */
    var baseUrl: String

    /**
     * url地址的后缀字符串
     */
    var urlSuffix: String

    /**
     * [baseUrl] 和 [urlSuffix] 组拼后的url
     */
    val url: String
        get() = baseUrl + urlSuffix

    /**
     * 设置请求标识
     */
    var tag: String?

    /**
     * 连接超时（毫秒），默认值[DEFAULT_CONNECT_TIMEOUT]
     */
    var connectTimeout: Int

    /**
     * 读取超时（毫秒），默认值[DEFAULT_READ_TIMEOUT]
     */
    var readTimeout: Int

    /**
     * 返回设置的请求参数
     */
    val params: HttpDataHolder<String, Any>

    /**
     * 返回设置的Header
     */
    val headers: HttpDataHolder<String, String>

    /** 扩展字段 */
    var extra: Any?

    /**
     * 是否拦截请求
     */
    var interceptExecute: Boolean

    /**
     * 是否拦截[IResultInterceptor]
     */
    var interceptResult: Boolean

    /**
     * 上传回调
     */
    var uploadProgressCallback: IUploadProgressCallback?

    var sSLSocketFactory: SSLSocketFactory?

    var hostnameVerifier: HostnameVerifier?

    /**
     * 异步请求
     */
    fun execute(callback: RequestCallback?): RequestHandler

    /**
     * 异步请求，在单线程线程池执行(发起的异步请求会按顺序一个个执行)
     */
    fun executeSequence(callback: RequestCallback?): RequestHandler

    /**
     * 同步请求
     */
    @Throws(HttpException::class)
    fun execute(): IResponse

    /**
     * 解析为实体
     */
    fun <T> parse(clazz: Class<T>, checkCancel: (() -> Boolean)? = null): FResult<T>

    companion object {
        /**默认连接超时*/
        const val DEFAULT_CONNECT_TIMEOUT = 15 * 1000

        /**默认读取超时*/
        const val DEFAULT_READ_TIMEOUT = 15 * 1000
    }
}
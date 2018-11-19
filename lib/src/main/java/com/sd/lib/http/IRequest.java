package com.sd.lib.http;

import com.sd.lib.http.callback.IUploadProgressCallback;
import com.sd.lib.http.callback.RequestCallback;
import com.sd.lib.http.utils.HttpDataHolder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public interface IRequest
{
    /**
     * 默认连接超时
     */
    int DEFAULT_CONNECT_TIMEOUT = 15 * 1000;
    /**
     * 默认读取超时
     */
    int DEFAULT_READ_TIMEOUT = 15 * 1000;

    /**
     * 设置基础url
     *
     * @param baseUrl
     * @return
     */
    IRequest setBaseUrl(String baseUrl);

    /**
     * 设置url地址的后缀字符串
     *
     * @param urlSuffix
     * @return
     */
    IRequest setUrlSuffix(String urlSuffix);

    /**
     * 设置请求标识
     *
     * @param tag
     * @return
     */
    IRequest setTag(String tag);

    /**
     * 设置请求连接超时，默认值{@link #DEFAULT_CONNECT_TIMEOUT}
     *
     * @param connectTimeout 毫秒
     * @return
     */
    IRequest setConnectTimeout(int connectTimeout);

    /**
     * 设置请求读取超时，默认值{@link #DEFAULT_READ_TIMEOUT}
     *
     * @param readTimeout 毫秒
     * @return
     */
    IRequest setReadTimeout(int readTimeout);

    /**
     * 设置上传回调
     *
     * @param callback
     * @return
     */
    IRequest setUploadProgressCallback(IUploadProgressCallback callback);

    IRequest setSSLSocketFactory(SSLSocketFactory SSLSocketFactory);

    IRequest setHostnameVerifier(HostnameVerifier hostnameVerifier);

    IRequest setInterceptExecute(boolean intercept);

    /**
     * 返回设置的请求参数
     *
     * @return
     */
    HttpDataHolder<String, Object> getParams();

    /**
     * 返回设置的Header
     *
     * @return
     */
    HttpDataHolder<String, String> getHeaders();

    /**
     * 返回设置的基础url{@link #setBaseUrl(String)}
     *
     * @return
     */
    String getBaseUrl();

    /**
     * 返回设置的url后缀{@link #setUrlSuffix(String)}
     *
     * @return
     */
    String getUrlSuffix();

    /**
     * 返回组拼后的基础url<br>
     * {@link #setBaseUrl(String)} 和 {@link #setUrlSuffix(String)} 组拼成的
     *
     * @return
     */
    String getUrl();

    /**
     * 返回设置的标识{@link #setTag(String)}
     *
     * @return
     */
    String getTag();

    SSLSocketFactory getSSLSocketFactory();

    HostnameVerifier getHostnameVerifier();

    /**
     * 异步请求
     *
     * @param callback
     * @return
     */
    RequestHandler execute(RequestCallback callback);

    /**
     * 异步请求，在单线程线程池执行(发起的异步请求会按顺序一个个执行)
     *
     * @param callback
     * @return
     */
    RequestHandler executeSequence(RequestCallback callback);

    /**
     * 同步请求
     *
     * @return
     * @throws Exception
     */
    IResponse execute() throws Exception;
}

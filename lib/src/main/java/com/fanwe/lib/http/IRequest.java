package com.fanwe.lib.http;

import com.fanwe.lib.http.callback.IUploadProgressCallback;
import com.fanwe.lib.http.callback.RequestCallback;
import com.fanwe.lib.http.utils.HttpDataHolder;

/**
 * Created by zhengjun on 2018/3/13.
 */
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
     * 设置请求的url
     *
     * @param url
     * @return
     */
    IRequest setUrl(String url);

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
     * 返回设置的url{@link #setUrl(String)}
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

package com.sd.lib.http.interceptor;

import com.sd.lib.http.IRequest;
import com.sd.lib.http.IResponse;

/**
 * 用于请求拦截
 */
public interface IRequestInterceptor
{
    /**
     * 发起网络请求之前回调
     *
     * @param request
     * @return 不为null，则用此方法的返回值
     * @throws Exception
     */
    IResponse beforeExecute(IRequest request) throws Exception;

    /**
     * 发起网络请求之后回调
     *
     * @param request
     * @param response
     * @return 不为null，则用此方法的返回值
     * @throws Exception
     */
    IResponse afterExecute(IRequest request, IResponse response) throws Exception;

    /**
     * 异常回调
     *
     * @param e
     */
    void onError(Exception e);
}

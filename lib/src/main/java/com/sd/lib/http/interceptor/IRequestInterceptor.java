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
     * @return 如果返回值不为null，则用此方法的返回值
     */
    IResponse beforeExecute(IRequest request);

    /**
     * 发起网络请求之后回调
     *
     * @param request
     * @param response
     * @return 返回值不能为null
     */
    IResponse afterExecute(IRequest request, IResponse response);
}

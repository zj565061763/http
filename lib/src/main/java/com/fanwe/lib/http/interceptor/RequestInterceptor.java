package com.fanwe.lib.http.interceptor;

import com.fanwe.lib.http.Request;
import com.fanwe.lib.http.Response;

/**
 * Created by zhengjun on 2017/10/11.
 */
public interface RequestInterceptor
{
    void beforeExecute(Request request);

    void afterExcute(Request request, Response response);
}

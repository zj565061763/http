package com.fanwe.lib.http.interceptor;

import com.fanwe.lib.http.Request;
import com.fanwe.lib.http.Response;

/**
 * Created by zhengjun on 2017/10/11.
 */
public interface RequestInterceptor
{
    RequestInterceptor EMPTY_REQUEST_INTERCEPTOR = new RequestInterceptor()
    {
        @Override
        public void beforeExecute(Request request)
        {
        }

        @Override
        public void afterExecute(Request request, Response response)
        {
        }
    };

    void beforeExecute(Request request);

    void afterExecute(Request request, Response response);
}

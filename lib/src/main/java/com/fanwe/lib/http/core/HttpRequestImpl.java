package com.fanwe.lib.http.core;

import com.fanwe.lib.http.HttpRequest;
import com.fanwe.lib.http.SDHttpRequest;

/**
 * Created by zhengjun on 2017/10/11.
 */

public abstract class HttpRequestImpl extends BaseRequest
{
    public HttpRequestImpl(String url)
    {
        super(url);
    }

    protected HttpRequest newHttpRequest(String url, String method)
    {
        SDHttpRequest request = new SDHttpRequest(url, method);
        request.headers(getMapHeader());
        return request;
    }
}

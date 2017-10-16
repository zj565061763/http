package com.fanwe.lib.http;

/**
 * Created by zhengjun on 2017/10/11.
 */
public class GetRequest extends HttpRequestImpl
{
    public GetRequest(String url)
    {
        super(url);
    }

    @Override
    protected void doExecute(Response response) throws Exception
    {
        HttpRequest request = newHttpRequest(HttpRequest.append(getUrl(), getMapParam()), HttpRequest.METHOD_GET);
        response.fillValue(request);
    }
}

package com.fanwe.lib.http;

/**
 * Created by zhengjun on 2017/10/11.
 */
public class GetRequest extends HttpRequestImpl
{
    protected GetRequest(String url)
    {
        super(url);
    }

    @Override
    protected Response onExecute() throws Exception
    {
        HttpRequest request = newHttpRequest(HttpRequest.append(getUrl(), getMapParam()), HttpRequest.METHOD_GET);

        Response response = new Response();
        response.fillValue(request);
        return response;
    }
}

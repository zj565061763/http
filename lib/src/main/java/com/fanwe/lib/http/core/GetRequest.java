package com.fanwe.lib.http.core;

import com.fanwe.lib.http.HttpRequest;

/**
 * Created by zhengjun on 2017/10/11.
 */
public class GetRequest extends BaseRequest
{
    public GetRequest(String url)
    {
        super(url);
    }

    @Override
    protected Response onExecute() throws Exception
    {
        String url = HttpRequest.append(getUrl(), getMapParam());
        HttpRequest httpRequest = newHttpRequest(url, HttpRequest.METHOD_GET);

        Response response = new Response();
        response.fillValue(httpRequest);
        return response;
    }
}

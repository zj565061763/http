package com.sd.lib.http.impl;

import com.sd.lib.http.IGetRequest;
import com.sd.lib.http.IResponse;

public class GetRequest extends BaseRequest implements IGetRequest
{
    @Override
    protected IResponse doExecute() throws Exception
    {
        HttpRequest request = newHttpRequest(HttpRequest.append(getUrl(), getParams().toMap()), HttpRequest.METHOD_GET);
        return new Response(request);
    }
}

package com.sd.lib.http.impl;

import com.sd.lib.http.IGetRequest;
import com.sd.lib.http.IResponse;

public class GetRequest extends BaseRequestImpl implements IGetRequest
{
    @Override
    protected IResponse doExecute() throws Exception
    {
        final HttpRequest request = newHttpRequest(HttpRequest.append(getUrl(), getParams().toMap()), HttpRequest.METHOD_GET);

        final Response response = new Response(request);
        response.getCodeOrThrow();

        return response;
    }
}

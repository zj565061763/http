package com.fanwe.www.http;

import com.fanwe.lib.http.Request;
import com.fanwe.lib.http.IRequestIdentifierProvider;

/**
 * 用来给Request生成唯一标识
 */
public class AppRequestIdentifierProvider implements IRequestIdentifierProvider
{
    @Override
    public String provideRequestIdentifier(Request request)
    {
        String identifier = null;

        Object ctl = request.getMapParam().get("ctl");
        Object act = request.getMapParam().get("act");
        if (ctl != null && act != null)
        {
            identifier = ctl + "," + act;
        }

        return identifier;
    }
}

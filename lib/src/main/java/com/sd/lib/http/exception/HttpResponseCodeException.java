package com.sd.lib.http.exception;

import java.io.IOException;

/**
 * Http返回码异常
 */
public class HttpResponseCodeException extends IOException
{
    private final int mCode;
    private final String mDetails;

    public HttpResponseCodeException(int code, String details)
    {
        mCode = code;
        mDetails = details;
    }

    public static HttpResponseCodeException from(int code)
    {
        if (code >= 400)
        {
            return new HttpResponseCodeException(code, null);
        }
        return null;
    }

    public int getCode()
    {
        return mCode;
    }

    public String getDetails()
    {
        return mDetails;
    }
}

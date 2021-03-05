package com.sd.lib.http.exception;

/**
 * Http返回码异常
 */
public class HttpExceptionResponseCode extends HttpException
{
    private final int mCode;
    private final String mDetails;

    public HttpExceptionResponseCode(int code, String details)
    {
        mCode = code;
        mDetails = details;
    }

    public static HttpExceptionResponseCode from(int code)
    {
        if (code >= 400)
        {
            return new HttpExceptionResponseCode(code, null);
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

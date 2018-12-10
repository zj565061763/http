package com.sd.lib.http.body;

import android.text.TextUtils;

import com.sd.lib.http.ContentType;

import java.nio.charset.Charset;

public class StringBody extends CharsetBody<String>
{
    private final String mBody;
    private final String mContentType;

    public StringBody(String body, String contentType)
    {
        this(body, null, contentType);
    }

    public StringBody(String body, Charset charset, String contentType)
    {
        super(charset);
        mBody = TextUtils.isEmpty(body) ? "" : body;
        mContentType = TextUtils.isEmpty(contentType) ? ContentType.STREAM : contentType;
    }

    @Override
    public final String getContentType()
    {
        return mContentType;
    }

    @Override
    public final String getBody()
    {
        return mBody;
    }
}

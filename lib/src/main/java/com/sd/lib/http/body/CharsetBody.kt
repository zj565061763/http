package com.sd.lib.http.body;

import java.nio.charset.Charset;

public abstract class CharsetBody<T> implements IRequestBody<T>
{
    private final Charset mCharset;

    public CharsetBody(Charset charset)
    {
        mCharset = charset == null ? Charset.defaultCharset() : charset;
    }

    public final Charset getCharset()
    {
        return mCharset;
    }
}

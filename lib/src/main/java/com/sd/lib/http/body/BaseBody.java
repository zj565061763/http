package com.sd.lib.http.body;

import com.sd.lib.http.ContentType;

public abstract class BaseBody<T> implements IRequestBody<T>
{
    @Override
    public String getContentType()
    {
        return ContentType.STREAM;
    }
}

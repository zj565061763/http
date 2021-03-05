package com.sd.lib.http.body;

import com.sd.lib.http.ContentType;

import java.nio.charset.Charset;

public class JsonBody extends StringBody
{
    public JsonBody(String body)
    {
        this(body, null);
    }

    public JsonBody(String body, Charset charset)
    {
        super(body, charset, ContentType.JSON);
    }
}

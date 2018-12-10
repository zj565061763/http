package com.sd.lib.http.body;

import com.sd.lib.http.ContentType;
import com.sd.lib.http.utils.HttpDataHolder;

public class FormBody extends BaseBody<HttpDataHolder<String, String>>
{
    private final HttpDataHolder<String, String> mBody;

    public FormBody(HttpDataHolder<String, String> body)
    {
        mBody = body;
    }

    @Override
    public final String getContentType()
    {
        return ContentType.FORM;
    }

    public final HttpDataHolder<String, String> getBody()
    {
        return mBody;
    }
}

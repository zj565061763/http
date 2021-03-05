package com.sd.lib.http.body;

import com.sd.lib.http.ContentType;

public class BytesBody implements IRequestBody<byte[]>
{
    private final byte[] mBody;

    public BytesBody(byte[] body)
    {
        mBody = body;
    }

    @Override
    public String getContentType()
    {
        return ContentType.STREAM;
    }

    @Override
    public final byte[] getBody()
    {
        return mBody;
    }
}

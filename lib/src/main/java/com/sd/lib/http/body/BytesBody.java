package com.sd.lib.http.body;

public class BytesBody extends BaseBody<byte[]>
{
    private final byte[] mBody;

    public BytesBody(byte[] body)
    {
        mBody = body;
    }

    @Override
    public final byte[] getBody()
    {
        return mBody;
    }
}

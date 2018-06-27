package com.fanwe.lib.http.body;

/**
 * Created by zhengjun on 2017/10/16.
 */

public class RequestBody
{
    private String mName;
    private String mContentType;

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        this.mName = name;
    }

    public String getContentType()
    {
        return mContentType;
    }

    public void setContentType(String contentType)
    {
        this.mContentType = contentType;
    }
}

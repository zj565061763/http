package com.fanwe.lib.http.body;

/**
 * Created by zhengjun on 2017/10/16.
 */

public class RequestBody
{
    private String name;
    private String filename;
    private String contentType;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getContentType()
    {
        return contentType;
    }

    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
}

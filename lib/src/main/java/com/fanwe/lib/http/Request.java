package com.fanwe.lib.http;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhengjun on 2017/10/11.
 */
public abstract class Request
{
    private String mUrl;

    private Map<Object, Object> mMapParam;
    private Map<String, String> mMapHeader;

    private Object mTag;

    protected Request(String url)
    {
        setUrl(url);
    }

    public Request setUrl(String url)
    {
        mUrl = url;
        return this;
    }

    public Request param(Object name, Object value)
    {
        getMapParam().put(name, value);
        return this;
    }

    public Request header(String name, String value)
    {
        getMapHeader().put(name, value);
        return this;
    }

    public Request setTag(Object tag)
    {
        mTag = tag;
        return this;
    }

    public Object getTag()
    {
        return mTag;
    }

    public String getUrl()
    {
        return mUrl;
    }

    public Map<Object, Object> getMapParam()
    {
        if (mMapParam == null)
        {
            mMapParam = new LinkedHashMap<>();
        }
        return mMapParam;
    }

    public Map<String, String> getMapHeader()
    {
        if (mMapHeader == null)
        {
            mMapHeader = new LinkedHashMap<>();
        }
        return mMapHeader;
    }

    public final Response execute() throws Exception
    {
        return onExecute();
    }

    protected abstract Response onExecute() throws Exception;
}

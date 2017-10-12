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

    public static GetRequest get(String url)
    {
        return new GetRequest(url);
    }

    public static PostRequest post(String url)
    {
        return new PostRequest(url);
    }

    /**
     * 设置请求的url
     *
     * @param url
     * @return
     */
    public Request setUrl(String url)
    {
        mUrl = url;
        return this;
    }

    /**
     * 设置请求参数
     *
     * @param name
     * @param value
     * @return
     */
    public Request param(Object name, Object value)
    {
        getMapParam().put(name, value);
        return this;
    }

    /**
     * 设置header参数
     *
     * @param name
     * @param value
     * @return
     */
    public Request header(String name, String value)
    {
        getMapHeader().put(name, value);
        return this;
    }

    /**
     * 设置请求对应的tag
     *
     * @param tag
     * @return
     */
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

    /**
     * 异步请求
     *
     * @param callback
     */
    public final void execute(RequestCallback callback)
    {
        RequestManager.getInstance().execute(this, callback);
    }

    /**
     * 同步请求
     *
     * @return
     * @throws Exception
     */
    public final Response execute() throws Exception
    {
        RequestManager.getInstance().beforeExecute(this);
        Response response = onExecute();
        RequestManager.getInstance().afterExcute(this, response);
        return response;
    }

    protected abstract Response onExecute() throws Exception;
}

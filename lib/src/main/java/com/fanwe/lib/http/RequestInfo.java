package com.fanwe.lib.http;

/**
 * 用于保存异步请求对应的信息
 * Created by zhengjun on 2017/10/17.
 */
class RequestInfo
{
    private String mTag;
    private String mRequestIdentifier;

    public String getTag()
    {
        return mTag;
    }

    public void setTag(String tag)
    {
        mTag = tag;
    }

    public String getRequestIdentifier()
    {
        return mRequestIdentifier;
    }

    public void setRequestIdentifier(String requestIdentifier)
    {
        mRequestIdentifier = requestIdentifier;
    }
}

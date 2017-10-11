package com.fanwe.lib.http.core;

/**
 * Created by zhengjun on 2017/10/11.
 */
public abstract class StringRequestCallback extends RequestCallback
{
    private String mResult;

    @Override
    public void onSuccessBackground() throws Exception
    {
        super.onSuccessBackground();
        mResult = getResponse().parseToString();
    }

    public String getResult()
    {
        return mResult;
    }
}

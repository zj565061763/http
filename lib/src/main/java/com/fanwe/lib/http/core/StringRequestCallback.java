package com.fanwe.lib.http.core;

/**
 * Created by zhengjun on 2017/10/11.
 */
public abstract class StringRequestCallback<String> extends RequestCallback
{
    @Override
    public void onSuccessBackground() throws Exception
    {
        super.onSuccessBackground();
        setResult(getResponse().parseToString());
    }
}

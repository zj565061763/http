package com.fanwe.lib.http;

import java.io.IOException;

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
        mResult = parseToString();
    }

    /**
     * 将返回结果转为字符串
     *
     * @return
     * @throws IOException
     */
    protected String parseToString() throws IOException
    {
        return getResponse().parseToString();
    }

    public String getResult()
    {
        return mResult;
    }
}

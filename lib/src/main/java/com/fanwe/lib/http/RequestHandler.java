package com.fanwe.lib.http;

/**
 * Created by zhengjun on 2017/10/17.
 */
public class RequestHandler
{
    private RequestTask mRequestTask;

    RequestHandler(RequestTask requestTask)
    {
        mRequestTask = requestTask;
    }

    public void cancel()
    {
        mRequestTask.cancel(true);
    }
}

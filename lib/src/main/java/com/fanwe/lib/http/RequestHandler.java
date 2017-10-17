package com.fanwe.lib.http;

/**
 * 执行异步请求后返回的对象，提供一些方法暴露给外部
 * Created by zhengjun on 2017/10/17.
 */
public class RequestHandler
{
    private RequestTask mRequestTask;

    RequestHandler(RequestTask requestTask)
    {
        mRequestTask = requestTask;
    }

    /**
     * 取消请求
     */
    public void cancel()
    {
        mRequestTask.cancel(true);
    }
}

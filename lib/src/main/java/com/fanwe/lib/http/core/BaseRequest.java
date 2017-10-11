package com.fanwe.lib.http.core;

import com.fanwe.lib.http.HttpRequest;
import com.fanwe.lib.http.SDHttpRequest;
import com.fanwe.lib.task.SDTask;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhengjun on 2017/10/11.
 */

public abstract class BaseRequest
{

    private String mUrl;
    private Map<Object, Object> mMapParam = new LinkedHashMap<>();

    public BaseRequest(String url)
    {
        mUrl = url;
    }

    public BaseRequest param(Object key, Object value)
    {
        mMapParam.put(key, value);
        return this;
    }

    public String getUrl()
    {
        return mUrl;
    }

    public Map<Object, Object> getMapParam()
    {
        return mMapParam;
    }

    protected HttpRequest newHttpRequest(String url, String method)
    {
        return new SDHttpRequest(url, method);
    }

    public abstract Response execute() throws Exception;

    private SDTask mTask;

    public void execute(final RequestCallback callback)
    {
        if (mTask != null)
        {
            throw new IllegalArgumentException("enqueue can not be invoke more than once");
        }
        callback.onStart();
        mTask = new SDTask()
        {
            @Override
            protected void onRun() throws Exception
            {
                Response response = execute();
                callback.setResponse(response);
                callback.onSuccessBackground();
                callback.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        callback.onSuccess();
                        callback.onFinish();
                    }
                });
            }

            @Override
            protected void onError(final Exception e)
            {
                super.onError(e);
                callback.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        callback.onError(e);
                        callback.onFinish();
                    }
                });
            }

            @Override
            protected void onCancelCalled()
            {
                super.onCancelCalled();
                callback.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        callback.onCancel();
                        callback.onFinish();
                    }
                });
            }
        };
        mTask.submit();
    }

    public void cancel()
    {
        if (mTask != null)
        {
            mTask.cancel();
        }
    }

}

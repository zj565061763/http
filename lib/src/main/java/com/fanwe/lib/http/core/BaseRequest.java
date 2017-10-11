package com.fanwe.lib.http.core;

import com.fanwe.lib.task.SDTask;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhengjun on 2017/10/11.
 */
public abstract class BaseRequest
{
    private String mUrl;

    private Map<Object, Object> mMapParam;
    private Map<String, String> mMapHeader;

    public static GetRequest get(String url)
    {
        return new GetRequest(url);
    }

    public static PostRequest post(String url)
    {
        return new PostRequest(url);
    }

    public BaseRequest(String url)
    {
        setUrl(url);
    }

    public BaseRequest setUrl(String url)
    {
        mUrl = url;
        return this;
    }

    public BaseRequest param(Object name, Object value)
    {
        getMapParam().put(name, value);
        return this;
    }

    public BaseRequest header(String name, String value)
    {
        getMapHeader().put(name, value);
        return this;
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

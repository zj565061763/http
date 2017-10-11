package com.fanwe.lib.http;

import com.fanwe.lib.task.SDTask;

/**
 * Created by zhengjun on 2017/10/11.
 */
class RequestTask extends SDTask
{
    private Request mRequest;
    private RequestCallback mCallback;

    public RequestTask(Request request, RequestCallback callback)
    {
        mRequest = request;
        if (callback == null)
        {
            callback = RequestCallback.EMPTY_CALLBACK;
        }
        mCallback = callback;

        callback.setRequest(request);
        callback.onStart();
    }

    public Request getRequest()
    {
        return mRequest;
    }

    public RequestCallback getCallback()
    {
        return mCallback;
    }

    @Override
    protected void onRun() throws Exception
    {
        Response response = getRequest().execute();

        getCallback().setResponse(response);
        getCallback().onSuccessBackground();
        RequestCallback.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                getCallback().onSuccess();
                getCallback().onFinish();
            }
        });
    }

    @Override
    protected void onError(final Exception e)
    {
        super.onError(e);
        RequestCallback.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                getCallback().onError(e);
                getCallback().onFinish();
            }
        });
    }

    @Override
    protected void onCancelCalled()
    {
        super.onCancelCalled();
        RequestCallback.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                getCallback().onCancel();
                getCallback().onFinish();
            }
        });
    }
}

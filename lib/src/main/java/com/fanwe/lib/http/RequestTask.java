package com.fanwe.lib.http;

import com.fanwe.lib.http.callback.IRequestCallback;
import com.fanwe.lib.http.callback.UploadProgressCallback;
import com.fanwe.lib.http.utils.TransmitParam;
import com.fanwe.lib.task.SDTask;

/**
 * Created by zhengjun on 2017/10/11.
 */
class RequestTask extends SDTask implements UploadProgressCallback
{
    private Request mRequest;
    private IRequestCallback mCallback;

    public RequestTask(Request request, IRequestCallback callback)
    {
        mRequest = request;
        mCallback = callback;

        request.setUploadProgressCallback(this);
    }

    public Request getRequest()
    {
        return mRequest;
    }

    public IRequestCallback getCallback()
    {
        if (mCallback == null)
        {
            mCallback = IRequestCallback.EMPTY_CALLBACK;
        }
        return mCallback;
    }

    @Override
    protected void onRun() throws Exception
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                getCallback().onStart();
            }
        });

        Response response = getRequest().execute();

        getCallback().setResponse(response);
        getCallback().onSuccessBackground();
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                getCallback().onSuccessBefore();
                getCallback().onSuccess();
            }
        });
    }

    @Override
    protected void onError(final Exception e)
    {
        super.onError(e);

        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (isCancelled())
                {
                    getCallback().onCancel();
                } else
                {
                    getCallback().onError(e);
                }
            }
        });
    }

    @Override
    protected void onFinally()
    {
        super.onFinally();
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                getCallback().onFinish();
            }
        });
    }

    @Override
    public void onProgressUpload(TransmitParam param)
    {
        getCallback().onProgressUpload(param);
    }
}

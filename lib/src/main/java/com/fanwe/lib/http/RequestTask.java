package com.fanwe.lib.http;

import com.fanwe.lib.http.callback.IRequestCallback;
import com.fanwe.lib.http.callback.IUploadProgressCallback;
import com.fanwe.lib.http.utils.TransmitParam;
import com.fanwe.lib.task.SDTask;

/**
 * Created by zhengjun on 2017/10/11.
 */
class RequestTask extends SDTask implements IUploadProgressCallback
{
    private Request mRequest;
    private IRequestCallback mCallback;

    public RequestTask(Request request, IRequestCallback callback)
    {
        mRequest = request;
        mCallback = callback;

        mCallback.setRequest(request);
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
            mCallback = IRequestCallback.DEFAULT;
        }
        return mCallback;
    }

    @Override
    protected void onRun() throws Exception
    {
        runOnUiThread(mStartRunnable);

        Response response = getRequest().execute();
        getCallback().setResponse(response);
        getCallback().onSuccessBackground();

        runOnUiThread(mSuccessRunnable);
    }

    private Runnable mStartRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            getCallback().onStart();
        }
    };

    private Runnable mSuccessRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            getCallback().onSuccessBefore();
            getCallback().onSuccess();
        }
    };

    private Runnable mFinishRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            getCallback().onFinish();
        }
    };

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
        runOnUiThread(mFinishRunnable);
    }

    @Override
    public void onProgressUpload(TransmitParam param)
    {
        getCallback().onProgressUpload(param);
    }
}

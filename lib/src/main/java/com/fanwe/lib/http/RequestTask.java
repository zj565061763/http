package com.fanwe.lib.http;

import com.fanwe.lib.http.callback.IRequestCallback;
import com.fanwe.lib.http.callback.IUploadProgressCallback;
import com.fanwe.lib.http.utils.LogUtil;
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

    private String getLogPrefix()
    {
        return "RequestTask" + this;
    }

    @Override
    protected void onRun() throws Exception
    {
        LogUtil.e(getLogPrefix() + " 1 onRun---------->:" + Thread.currentThread().getName());

        synchronized (RequestTask.this)
        {
            runOnUiThread(mStartRunnable);
            LogUtil.i(getLogPrefix() + " 2 pauseThread:" + Thread.currentThread().getName());
            RequestTask.this.wait(); //等待开始回调完成
        }

        LogUtil.i(getLogPrefix() + " 4 resumeThread:" + Thread.currentThread().getName());

        Response response = getRequest().execute();
        getCallback().setResponse(response);
        getCallback().onSuccessBackground();

        LogUtil.i(getLogPrefix() + " 5 onSuccess:" + Thread.currentThread().getName());

        runOnUiThread(mSuccessRunnable);
    }

    private Runnable mStartRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            getCallback().onStart();

            synchronized (RequestTask.this)
            {
                LogUtil.i(getLogPrefix() + " 3 notifyAll:" + Thread.currentThread().getName());
                RequestTask.this.notifyAll();
            }
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

    @Override
    protected void onError(final Exception e)
    {
        super.onError(e);
        if (isCancelled())
        {
            getCallback().onCancel();
        } else
        {
            getCallback().onError(e);
        }
    }

    @Override
    protected void onFinally()
    {
        super.onFinally();
        getCallback().onFinish();
    }

    @Override
    public void onProgressUpload(TransmitParam param)
    {
        getCallback().onProgressUpload(param);
    }
}

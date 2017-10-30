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

    private synchronized void pauseThread() throws InterruptedException
    {
        LogUtil.i("RequestTask pauseThread:" + Thread.currentThread().getName());
        wait();
    }

    private synchronized void resumeThread()
    {
        notifyAll();
    }

    @Override
    protected void onRun() throws Exception
    {
        LogUtil.i("RequestTask onRun---------->:" + Thread.currentThread().getName());
        runOnUiThread(mStartRunnable);
        pauseThread(); //等待开始回调完成
        LogUtil.i("RequestTask resumeThread:" + Thread.currentThread().getName());

        Response response = getRequest().execute();
        getCallback().setResponse(response);
        getCallback().onSuccessBackground();

        LogUtil.i("RequestTask onSuccess:" + Thread.currentThread().getName());

        runOnUiThread(mSuccessRunnable);
    }

    private Runnable mStartRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            getCallback().onStart();
            resumeThread();
        }
    };

    private Runnable mSuccessRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            getCallback().onSuccessBefore();
            getCallback().onSuccess();
            getCallback().onFinish();
        }
    };

    private Runnable mCancelRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            getCallback().onCancel();
            getCallback().onFinish();
        }
    };

    @Override
    protected void onError(final Exception e)
    {
        super.onError(e);

        if (isCancelled())
        {
            runOnUiThread(mCancelRunnable);
        } else
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    getCallback().onError(e);
                    getCallback().onFinish();
                }
            });
        }
    }

    @Override
    public void onProgressUpload(TransmitParam param)
    {
        getCallback().onProgressUpload(param);
    }
}

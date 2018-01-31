package com.fanwe.lib.http;

import com.fanwe.lib.http.callback.IUploadProgressCallback;
import com.fanwe.lib.http.callback.RequestCallback;
import com.fanwe.lib.http.utils.LogUtil;
import com.fanwe.lib.http.utils.TransmitParam;
import com.fanwe.lib.task.FTask;

/**
 * Created by zhengjun on 2017/10/11.
 */
class RequestTask extends FTask implements IUploadProgressCallback
{
    private final Request mRequest;
    private final RequestCallback mCallback;

    public RequestTask(Request request, RequestCallback callback)
    {
        mRequest = request;
        mCallback = callback;

        mCallback.setRequest(mRequest);
        mRequest.setUploadProgressCallback(this);
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

        final Response response = mRequest.execute();
        mCallback.setResponse(response);
        mCallback.onSuccessBackground();

        LogUtil.i(getLogPrefix() + " 5 onSuccess:" + Thread.currentThread().getName());

        runOnUiThread(mSuccessRunnable);
    }

    private Runnable mStartRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            synchronized (RequestTask.this)
            {
                mCallback.onStart();
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
            mCallback.onSuccessBefore();
            mCallback.onSuccess();
        }
    };

    @Override
    protected void onError(final Exception e)
    {
        super.onError(e);
        if (isCancelled())
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mCallback.onCancel();
                }
            });
        } else
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mCallback.onError(e);
                }
            });
        }
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
                mCallback.onFinish();
            }
        });
    }

    @Override
    public void onProgressUpload(TransmitParam param)
    {
        mCallback.onProgressUpload(param);
    }
}

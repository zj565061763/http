package com.sd.lib.http;

import com.sd.lib.http.callback.IUploadProgressCallback;
import com.sd.lib.http.callback.RequestCallback;
import com.sd.lib.http.utils.HttpLog;
import com.sd.lib.http.utils.TransmitParam;

class RequestTask extends FTask implements IUploadProgressCallback
{
    private final IRequest mRequest;
    private final RequestCallback mCallback;

    public RequestTask(IRequest request, RequestCallback callback)
    {
        mRequest = request;
        mCallback = callback;

        mRequest.setUploadProgressCallback(this);
    }

    private String getLogPrefix()
    {
        return String.valueOf(this);
    }

    @Override
    protected void onRun() throws Throwable
    {
        HttpLog.i(getLogPrefix() + " 1 onRun---------->");

        synchronized (RequestTask.this)
        {
            runOnUiThread(mStartRunnable);
            HttpLog.i(getLogPrefix() + " 2 waitThread");
            RequestTask.this.wait(); //等待开始回调完成
        }

        final State state = getState();
        HttpLog.i(getLogPrefix() + " 4 resumeThread state:" + state);
        if (state == State.DoneCancel)
            return;

        final IResponse response = mRequest.execute();
        mCallback.setResponse(response);
        mCallback.onSuccessBackground();

        HttpLog.i(getLogPrefix() + " 5 executed");
        runOnUiThread(mSuccessRunnable);
    }

    private final Runnable mStartRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            synchronized (RequestTask.this)
            {
                mCallback.onStart();
                HttpLog.i(getLogPrefix() + " 3 notifyThread");
                RequestTask.this.notifyAll();
            }
        }
    };

    private final Runnable mSuccessRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mCallback.onSuccessBefore();
            mCallback.onSuccess();
        }
    };

    @Override
    protected void onError(final Throwable e)
    {
        super.onError(e);
        if (getState() == State.DoneError)
        {
            HttpLog.i(getLogPrefix() + " onError:" + e);
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mCallback.onError(e);
                }
            });
        } else
        {
            HttpLog.e(getLogPrefix() + "receive error:" + e + " when state:" + getState());
        }
    }

    @Override
    protected void onCancel()
    {
        super.onCancel();
        HttpLog.i(getLogPrefix() + " onCancel");
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mCallback.onCancel();
            }
        });
    }

    @Override
    protected void onFinish()
    {
        super.onFinish();
        HttpLog.i(getLogPrefix() + " onFinish");
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

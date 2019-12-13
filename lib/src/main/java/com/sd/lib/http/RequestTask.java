package com.sd.lib.http;

import com.sd.lib.http.callback.IUploadProgressCallback;
import com.sd.lib.http.callback.RequestCallback;
import com.sd.lib.http.utils.HttpLog;
import com.sd.lib.http.utils.TransmitParam;

class RequestTask extends FTask implements IUploadProgressCallback
{
    private final IRequest mRequest;
    private final RequestCallback mCallback;

    private volatile boolean mIsStartNotified = false;

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
        synchronized (RequestTask.this)
        {
            HttpLog.i(getLogPrefix() + " 1 onRun");
            if (!mIsStartNotified)
            {
                // 等待开始回调完成
                HttpLog.i(getLogPrefix() + " wait start...");
                RequestTask.this.wait();
                HttpLog.i(getLogPrefix() + " wait finish");
            }
        }

        HttpLog.i(getLogPrefix() + " 2 execute before state:" + getState());
        if (getState() == State.DoneCancel)
            return;

        final IResponse response = mRequest.execute();

        HttpLog.i(getLogPrefix() + " 3 execute after state:" + getState());
        if (getState() == State.DoneCancel)
            return;

        mCallback.setResponse(response);
        mCallback.onSuccessBackground();

        HttpLog.i(getLogPrefix() + " 4 onSuccessBackground state:" + getState());
        if (getState() == State.DoneCancel)
            return;

        HttpLog.i(getLogPrefix() + " 5 success");
        runOnUiThread(mSuccessRunnable);
    }

    @Override
    protected void onSubmit()
    {
        super.onSubmit();
        HttpLog.i(getLogPrefix() + " onSubmit---------->");
        runOnUiThread(mStartRunnable);
    }

    private final Runnable mStartRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (getState() == State.DoneCancel)
            {
                // 如果被取消的话，此处不通知开始事件，由取消回调中通知开始事件
                HttpLog.e(getLogPrefix() + " start runnable run with state:" + getState());
                return;
            }

            synchronized (RequestTask.this)
            {
                mCallback.onStart();
                mIsStartNotified = true;
                HttpLog.i(getLogPrefix() + " onStart");
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
        synchronized (RequestTask.this)
        {
            if (mIsStartNotified)
            {
                HttpLog.i(getLogPrefix() + " onCancel");
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
                HttpLog.e(getLogPrefix() + " onCancel need onStart");
                mIsStartNotified = true;
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mCallback.onStart();
                        mCallback.onCancel();
                    }
                });
            }
        }
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

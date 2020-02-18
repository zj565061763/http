package com.sd.lib.http;

import com.sd.lib.http.callback.IUploadProgressCallback;
import com.sd.lib.http.callback.RequestCallback;
import com.sd.lib.http.utils.HttpLog;
import com.sd.lib.http.utils.TransmitParam;

final class RequestTask extends FTask implements IUploadProgressCallback
{
    private final IRequest mRequest;
    private final RequestCallback mRequestCallback;

    private final Callback mCallback;

    private volatile boolean mIsStartNotified = false;

    public RequestTask(IRequest request, RequestCallback requestCallback, Callback callback)
    {
        mRequest = request;
        mRequestCallback = requestCallback;
        mCallback = callback;

        mRequest.setUploadProgressCallback(this);
    }

    private String getLogPrefix()
    {
        return String.valueOf(this);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        HttpLog.i(getLogPrefix() + " cancel called state:" + getState());
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    protected void onRun() throws Exception
    {
        HttpLog.i(getLogPrefix() + " 1 onRun state:" + getState());
        if (checkCancel())
            return;

        runOnUiThread(mStartRunnable);
        synchronized (RequestTask.this)
        {
            if (!mIsStartNotified)
            {
                // 等待开始回调完成
                HttpLog.i(getLogPrefix() + " wait start...");
                RequestTask.this.wait();
                HttpLog.i(getLogPrefix() + " wait finish");
            }
        }

        HttpLog.i(getLogPrefix() + " 2 before execute state:" + getState());
        if (checkCancel())
            return;

        final IResponse response = mRequest.execute();

        HttpLog.i(getLogPrefix() + " 3 after execute state:" + getState());
        if (checkCancel())
            return;

        mRequestCallback.setResponse(response);
        mRequestCallback.onSuccessBackground();

        HttpLog.i(getLogPrefix() + " 4 after onSuccessBackground state:" + getState());
        if (checkCancel())
            return;

        HttpLog.i(getLogPrefix() + " 5 success state:" + getState());
        runOnUiThread(mSuccessRunnable);
    }

    private boolean checkCancel()
    {
        if (getState() == State.DoneCancel)
        {
            HttpLog.i(getLogPrefix() + " check cancel !!!");
            return true;
        }
        return false;
    }

    private final Runnable mStartRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (getState() == State.DoneCancel)
            {
                // 如果被取消的话，此处不通知开始事件
                HttpLog.e(getLogPrefix() + " start runnable but state:" + getState());
                return;
            }

            synchronized (RequestTask.this)
            {
                if (!mIsStartNotified)
                {
                    mIsStartNotified = true;
                    HttpLog.i(getLogPrefix() + " notify onStart");
                    mRequestCallback.onStart();
                }

                RequestTask.this.notifyAll();
            }
        }
    };

    private final Runnable mSuccessRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (getState() == State.DoneCancel)
            {
                HttpLog.e(getLogPrefix() + " success runnable but state:" + getState());
                return;
            }

            mRequestCallback.onSuccessBefore();
            mRequestCallback.onSuccess();
        }
    };

    @Override
    protected void onError(final Exception e)
    {
        if (getState() == State.DoneError)
        {
            HttpLog.i(getLogPrefix() + " onError:" + e);
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    mRequestCallback.onError(e);
                }
            });
        } else
        {
            HttpLog.e(getLogPrefix() + " receive error:" + e + " when state:" + getState());
        }
    }

    @Override
    protected void onCancel()
    {
        super.onCancel();
        HttpLog.i(getLogPrefix() + " onCancel mIsStartNotified:" + mIsStartNotified);
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mRequestCallback.onCancel();
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
                mRequestCallback.onFinish();
            }
        });

        if (mCallback != null)
            mCallback.onFinish(this);
    }

    @Override
    public void onProgressUpload(TransmitParam param)
    {
        mRequestCallback.onProgressUpload(param);
    }

    public interface Callback
    {
        void onFinish(RequestTask task);
    }
}

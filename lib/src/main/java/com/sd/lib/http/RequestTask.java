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
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        HttpLog.i(getLogPrefix() + " cancel called state:" + getState());
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    protected void onRun() throws Throwable
    {
        HttpLog.i(getLogPrefix() + " 1 onRun state:" + getState());
        if (getState() == State.DoneCancel)
        {
            HttpLog.i(getLogPrefix() + " check state !!!");
            return;
        }

        runOnUiThread(mStartRunnable);

        synchronized (RequestTask.this)
        {
            if (!mIsStartNotified)
            {
                // 等待开始回调完成
                HttpLog.i(getLogPrefix() + " wait start...");
                try
                {
                    RequestTask.this.wait();
                } catch (InterruptedException e)
                {
                    HttpLog.e(getLogPrefix() + " wait interrupted state:" + getState());
                    if (getState() == State.DoneCancel)
                    {
                        HttpLog.i(getLogPrefix() + " check state !!!");
                        return;
                    }
                }
                HttpLog.i(getLogPrefix() + " wait finish");
            }
        }

        HttpLog.i(getLogPrefix() + " 2 execute before state:" + getState());
        if (getState() == State.DoneCancel)
        {
            HttpLog.i(getLogPrefix() + " check state !!!");
            return;
        }

        final IResponse response = mRequest.execute();

        HttpLog.i(getLogPrefix() + " 3 execute after state:" + getState());
        if (getState() == State.DoneCancel)
        {
            HttpLog.i(getLogPrefix() + " check state !!!");
            return;
        }

        mCallback.setResponse(response);
        mCallback.onSuccessBackground();

        HttpLog.i(getLogPrefix() + " 4 onSuccessBackground state:" + getState());
        if (getState() == State.DoneCancel)
        {
            HttpLog.i(getLogPrefix() + " check state !!!");
            return;
        }

        HttpLog.i(getLogPrefix() + " 5 notify success state:" + getState());

        runOnUiThread(mSuccessRunnable);
    }

    private final Runnable mStartRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (getState() == State.DoneCancel)
            {
                // 如果被取消的话，此处不通知开始事件，由取消回调中通知开始事件
                HttpLog.e(getLogPrefix() + " start runnable but state:" + getState());
                return;
            }

            synchronized (RequestTask.this)
            {
                if (!mIsStartNotified)
                {
                    mIsStartNotified = true;
                    mCallback.onStart();
                    HttpLog.i(getLogPrefix() + " onStart");
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
            HttpLog.e(getLogPrefix() + " receive error:" + e + " when state:" + getState());
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

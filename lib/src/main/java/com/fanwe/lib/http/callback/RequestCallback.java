package com.fanwe.lib.http.callback;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Administrator on 2017/10/10.
 */

public abstract class RequestCallback
{
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable)
    {
        if (Looper.myLooper() == Looper.getMainLooper())
        {
            runnable.run();
        } else
        {
            MAIN_HANDLER.post(runnable);
        }
    }

    protected void onStart()
    {
    }

    protected abstract void onSuccess();

    protected void onError()
    {
    }

    protected void onCancel()
    {
    }

    protected void onFinish()
    {
    }

    public void notifyStart()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                onStart();
            }
        });
    }

    public void notifySuccess()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                onSuccess();
            }
        });
    }

    public void notifyError()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                onError();
            }
        });
    }

    public void notifyCancel()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                onCancel();
            }
        });
    }

    public void notifyFinish()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                onFinish();
            }
        });
    }
}

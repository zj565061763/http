package com.fanwe.lib.http.core;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by zhengjun on 2017/10/10.
 */

public abstract class RequestCallback
{
    private Request mRequest;
    private Response mResponse;

    final void setRequest(Request request)
    {
        mRequest = request;
    }

    public final Request getRequest()
    {
        return mRequest;
    }

    final void setResponse(Response response)
    {
        mResponse = response;
    }

    public final Response getResponse()
    {
        return mResponse;
    }

    public void onStart()
    {
    }

    public void onSuccessBackground() throws Exception
    {

    }

    public abstract void onSuccess();

    public void onError(Exception e)
    {
    }

    public void onCancel()
    {
    }

    public void onFinish()
    {
    }

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
}

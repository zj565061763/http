package com.fanwe.lib.http.utils;

import android.util.Log;

import com.fanwe.lib.http.RequestManager;

/**
 * Created by zhengjun on 2017/9/20.
 */
public class HttpLog
{
    public static final String TAG = "FHttp";

    public static boolean isDebug()
    {
        return RequestManager.getInstance().isDebug();
    }

    public static void i(String msg)
    {
        if (!isDebug())
        {
            return;
        }
        Log.i(TAG, msg);
    }

    public static void e(String msg)
    {
        if (!isDebug())
        {
            return;
        }
        Log.e(TAG, msg);
    }
}

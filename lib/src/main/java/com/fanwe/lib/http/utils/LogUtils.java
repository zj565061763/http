package com.fanwe.lib.http.utils;

import android.util.Log;

/**
 * Created by zhengjun on 2017/9/20.
 */

public class LogUtils
{
    public static final String TAG = "SDHttp";

    private static boolean mIsDebug;

    public static void setDebug(boolean debug)
    {
        mIsDebug = debug;
    }

    public static boolean isDebug()
    {
        return mIsDebug;
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

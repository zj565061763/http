package com.fanwe.www.http;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Administrator on 2017/10/27.
 */

public class App extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this))
        {
            return;
        }
        LeakCanary.install(this);
    }
}

package com.sd.www.http;

import android.app.Application;

import com.sd.lib.http.RequestManager;

public class App extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        RequestManager.getInstance().setDebug(true);
    }
}

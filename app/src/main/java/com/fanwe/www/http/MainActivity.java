package com.fanwe.www.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fanwe.lib.http.HttpRequest;
import com.fanwe.lib.http.SDHttpRequest;
import com.fanwe.lib.http.cookie.SharedPreferencesCookieJar;
import com.fanwe.lib.task.SDTask;

import java.net.HttpCookie;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "MainActivity";

    public static final String URL = "http://ilvbt3.fanwe.net/mapi/index.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SDHttpRequest.setCookieJar(new SharedPreferencesCookieJar(this));

        new SDTask()
        {
            @Override
            protected void onRun() throws Exception
            {
                SDHttpRequest request = (SDHttpRequest) new SDHttpRequest(URL, HttpRequest.METHOD_POST).form("ctl", "app").form("act", "init");

                if (request.ok())
                {
                    List<HttpCookie> listCookie = request.getResponseCookie();
                    Log.i(TAG, "cookie:" + listCookie);
                }

                Log.i(TAG, request.body());
            }
        }.submit();
    }
}

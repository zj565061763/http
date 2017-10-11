package com.fanwe.www.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.fanwe.lib.http.SDHttpRequest;
import com.fanwe.lib.http.cookie.SharedPreferencesCookieJar;
import com.fanwe.lib.http.core.PostRequest;
import com.fanwe.lib.http.core.StringRequestCallback;

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

        new PostRequest(URL).param("ctl", "app").param("act", "init").execute(new StringRequestCallback()
        {
            @Override
            public void onStart()
            {
                super.onStart();
                Log.i(TAG, "onStart");
            }

            @Override
            public void onSuccessBackground() throws Exception
            {
                super.onSuccessBackground();
                Log.i(TAG, "onSuccessBackground:" + getResult());
            }

            @Override
            public void onSuccess()
            {
                Log.i(TAG, "onSuccess");
            }

            @Override
            public void onError(Exception e)
            {
                super.onError(e);
                Log.i(TAG, "onError:" + e);
            }

            @Override
            public void onCancel()
            {
                super.onCancel();
                Log.i(TAG, "onCancel");
            }

            @Override
            public void onFinish()
            {
                super.onFinish();
                Log.i(TAG, "onFinish");
            }
        });
    }
}

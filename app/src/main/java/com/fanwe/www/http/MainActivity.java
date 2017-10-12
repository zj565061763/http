package com.fanwe.www.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.fanwe.lib.http.Request;
import com.fanwe.lib.http.RequestManager;
import com.fanwe.lib.http.Response;
import com.fanwe.lib.http.StringRequestCallback;
import com.fanwe.lib.http.cookie.SharedPreferencesCookieJar;
import com.fanwe.lib.http.interceptor.RequestInterceptor;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "MainActivity";

    public static final String URL = "http://ilvbt3.fanwe.net/mapi/index.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestManager.getInstance().setCookieJar(new SharedPreferencesCookieJar(this));
        RequestManager.getInstance().addRequestInterceptor(mRequestInterceptor);
    }

    private RequestInterceptor mRequestInterceptor = new RequestInterceptor()
    {
        @Override
        public void beforeExecute(Request request)
        {
            Log.i(TAG, "beforeExecute:" + request);
        }

        @Override
        public void afterExecute(Response response)
        {
            Log.i(TAG, "afterExecute:" + response.getRequest());
        }
    };

    public void onClickRequest(View view)
    {
        Request request = Request.get(URL).param("ctl", "app").param("act", "init").setTag(this);
        request.execute(new StringRequestCallback()
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
                Log.i(TAG, "onSuccessBackground");
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

    public void onClickCancelRequest(View view)
    {
        RequestManager.getInstance().cancelRequest(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        RequestManager.getInstance().cancelRequest(this);
        RequestManager.getInstance().removeRequestInterceptor(mRequestInterceptor);
    }
}

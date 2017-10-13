package com.fanwe.www.http;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.fanwe.lib.http.Request;
import com.fanwe.lib.http.RequestManager;
import com.fanwe.lib.http.Response;
import com.fanwe.lib.http.cookie.SerializableCookieStore;
import com.fanwe.lib.http.interceptor.RequestInterceptor;
import com.fanwe.lib.http.utils.LogUtils;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LogUtils.setDebug(true);

        //设置cookie管理对象
        RequestManager.getInstance().setCookieStore(new SerializableCookieStore(this));

        //设置请求拦截对象，可用于log输出，或者一些需要全局处理的逻辑
        RequestManager.getInstance().addRequestInterceptor(mRequestInterceptor);
    }

    private RequestInterceptor mRequestInterceptor = new RequestInterceptor()
    {
        @Override
        public void beforeExecute(Request request)
        {
            //请求发起之前回调
            Log.i(TAG, "beforeExecute:" + request);
        }

        @Override
        public void afterExecute(Response response)
        {
            //请求发起之后回调
            Log.i(TAG, "afterExecute:" + response.getRequest());
        }
    };

    public void onClickAsyncRequestActivity(View view)
    {
        startActivity(new Intent(this, AsyncRequestActivity.class));
    }

    public void onClickSyncRequestActivity(View view)
    {
        startActivity(new Intent(this, SyncRequestActivity.class));
    }

    public void onClickDownloadActivity(View view)
    {
        startActivity(new Intent(this, DownloadActivity.class));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        RequestManager.getInstance().removeRequestInterceptor(mRequestInterceptor);
    }
}

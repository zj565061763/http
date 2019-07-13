package com.sd.www.http.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sd.lib.http.RequestManager;
import com.sd.lib.http.cookie.SerializableCookieStore;
import com.sd.www.http.AppRequestInterceptor;
import com.sd.www.http.R;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置调试模式，输出log
        RequestManager.getInstance().setDebug(true);

        //设置cookie管理对象
        RequestManager.getInstance().setCookieStore(new SerializableCookieStore(this));

        //设置请求拦截对象，可用于log输出，或者一些需要全局处理的逻辑，注意这边传入的对象如果是和资源相关的对象，需要在资源销毁的时候remove
        RequestManager.getInstance().setRequestInterceptor(new AppRequestInterceptor());
    }

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
    }
}

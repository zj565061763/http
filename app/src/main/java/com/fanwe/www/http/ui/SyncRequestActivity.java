package com.fanwe.www.http.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.fanwe.lib.http.PostRequest;
import com.fanwe.lib.http.Request;
import com.fanwe.lib.http.Response;
import com.fanwe.www.http.R;

import java.io.InputStream;

/**
 * 同步请求demo
 */
public class SyncRequestActivity extends AppCompatActivity
{
    public static final String TAG = "SyncRequestActivity";

    public static final String URL = "http://ilvbt3.fanwe.net/mapi/index.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_request);
    }

    public void onClickRequest(View view)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Request request = new PostRequest(URL); //创建请求对象
                    request.param("ctl", "app").param("act", "init"); //创建要提交的form数据
                    Response response = request.execute(); //发起请求，得到Response对象

                    InputStream inputStream = response.getInputStream(); //结果以输入流返回
                    int code = response.getCode(); //返回码
                    String result = response.getBody(); //结果以字符串返回

                    Log.i(TAG, result);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

package com.sd.www.http.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.sd.lib.http.IRequest;
import com.sd.lib.http.IResponse;
import com.sd.lib.http.RequestHandler;
import com.sd.lib.http.RequestManager;
import com.sd.lib.http.callback.ModelRequestCallback;
import com.sd.lib.http.impl.GetRequest;
import com.sd.www.http.R;
import com.sd.www.http.model.WeatherModel;

import java.lang.reflect.Type;

/**
 * 异步请求demo
 */
public class AsyncRequestActivity extends AppCompatActivity
{
    public static final String TAG = AsyncRequestActivity.class.getSimpleName();
    public static final String URL = "http://www.weather.com.cn/data/cityinfo/101010100.html";

    private RequestHandler mRequestHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_request);
    }

    public void onClickRequest(View view)
    {
        IRequest request = new GetRequest();
        // 设置请求地址
        request.setBaseUrl(URL);
        // 设置请求参数
        request.getParams().put("aaa", "aaa").put("bbb", "bbb");
        // 设置该请求的tag，可用于取消请求
        request.setTag(TAG);

        // 发起异步请求
        mRequestHandler = request.execute(new ModelRequestCallback<WeatherModel>()
        {
            @Override
            public void onPrepare(IRequest request)
            {
                super.onPrepare(request);
                // 请求在被执行之前的准备回调(发起请求被调用的线程)
                Log.i(TAG, "onPrepare");
            }

            @Override
            public void onStart()
            {
                super.onStart();
                // 请求开始执行(UI线程)
                Log.i(TAG, "onStart");
            }

            @Override
            public void onSuccessBackground() throws Exception
            {
                super.onSuccessBackground();
                // 成功回调，super里面回调了parseToModel方法把返回的内容转为实体，(非UI线程)
                Log.i(TAG, "onSuccessBackground");
            }

            @Override
            protected WeatherModel parseToModel(String content, Type type)
            {
                // 把返回的内容转实体(非UI线程)
                return new Gson().fromJson(content, type);
            }

            @Override
            public void onSuccess()
            {
                // 成功回调(UI线程)

                // 获得返回结果对象
                IResponse response = getResponse();
                // 获得接口对应的实体
                WeatherModel model = getActModel();
                Log.i(TAG, "onSuccess:" + model.weatherinfo.city);
            }

            @Override
            public void onError(Exception e)
            {
                super.onError(e);
                // 异常回调，请求异常或者转实体出错(UI线程)
                Log.i(TAG, "onError:" + e);
            }

            @Override
            public void onCancel()
            {
                super.onCancel();
                // 请求被取消回调(UI线程)
                Log.i(TAG, "onCancel");
            }

            @Override
            public void onFinish()
            {
                super.onFinish();
                // 结束回调(UI线程)
                Log.i(TAG, "onFinish");
            }
        });
    }

    public void onClickCancelRequest(View view)
    {
        RequestManager.getInstance().cancelTag(TAG);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        RequestManager.getInstance().cancelTag(TAG);
        Log.i(TAG, "onDestroy");
    }
}

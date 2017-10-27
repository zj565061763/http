package com.fanwe.www.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.fanwe.lib.http.PostRequest;
import com.fanwe.lib.http.RequestManager;
import com.fanwe.lib.http.Response;
import com.fanwe.lib.http.callback.ModelRequestCallback;
import com.fanwe.lib.http.callback.RequestCallbackProxy;
import com.fanwe.www.http.model.InitActModel;
import com.google.gson.Gson;

/**
 * 异步请求demo
 */
public class AsyncRequestActivity extends AppCompatActivity
{
    public static final String TAG = "AsyncRequestActivity";

    public static final String URL = "http://ilvbt3.fanwe.net/mapi/index.php";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_request);
    }

    public void onClickRequest(View view)
    {
        new PostRequest(URL).param("ctl", "app").param("act", "init") //设置要提交的参数
                .setTag(TAG) //设置该请求对应的tag，可用于取消请求
//                .execute(mModelRequestCallback_0);
                .execute(RequestCallbackProxy.get(mModelRequestCallback_0, mModelRequestCallback_1)); //设置请求结果回调，可以设置多个回调
    }

    private ModelRequestCallback mModelRequestCallback_0 = new ModelRequestCallback<InitActModel>()
    {
        @Override
        public void onStart()
        {
            super.onStart();
            Log.i(TAG, "onStart_0");
        }

        @Override
        public void onSuccessBackground() throws Exception
        {
            super.onSuccessBackground();
            Log.i(TAG, "onSuccessBackground_0");
        }

        @Override
        protected InitActModel parseToModel(String content, Class<InitActModel> clazz)
        {
            return new Gson().fromJson(content, clazz);
        }

        @Override
        public void onSuccess()
        {
            Response response = getResponse(); //获得返回结果对象
            InitActModel model = getActModel();
            Log.i(TAG, "onSuccess_0:" + model.getCity());
        }

        @Override
        public void onError(Exception e)
        {
            super.onError(e);
            Log.i(TAG, "onError_0:" + e);
        }

        @Override
        public void onCancel()
        {
            super.onCancel();
            Log.i(TAG, "onCancel_0");
        }

        @Override
        public void onFinish()
        {
            super.onFinish();
            Log.i(TAG, "onFinish_0");
        }
    };

    private ModelRequestCallback mModelRequestCallback_1 = new ModelRequestCallback<InitActModel>()
    {
        @Override
        public void onStart()
        {
            super.onStart();
            Log.i(TAG, "onStart_1");
        }

        @Override
        public void onSuccessBackground() throws Exception
        {
            super.onSuccessBackground();
            Log.i(TAG, "onSuccessBackground_1");
        }

        @Override
        protected InitActModel parseToModel(String content, Class<InitActModel> clazz)
        {
            return new Gson().fromJson(content, clazz);
        }

        @Override
        public void onSuccess()
        {
            InitActModel model = getActModel();
            Log.i(TAG, "onSuccess_1:" + model.getCity());
        }

        @Override
        public void onError(Exception e)
        {
            super.onError(e);
            Log.i(TAG, "onError_1:" + e);
        }

        @Override
        public void onCancel()
        {
            super.onCancel();
            Log.i(TAG, "onCancel_1");
        }

        @Override
        public void onFinish()
        {
            super.onFinish();
            Log.i(TAG, "onFinish_1");
        }
    };

    public void onClickCancelRequest(View view)
    {
        RequestManager.getInstance().cancelTag(TAG);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        RequestManager.getInstance().cancelTag(TAG);
    }
}

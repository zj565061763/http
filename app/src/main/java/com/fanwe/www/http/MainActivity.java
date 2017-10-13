package com.fanwe.www.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.fanwe.lib.http.Request;
import com.fanwe.lib.http.RequestManager;
import com.fanwe.lib.http.Response;
import com.fanwe.lib.http.callback.FileRequestCallback;
import com.fanwe.lib.http.callback.ModelRequestCallback;
import com.fanwe.lib.http.cookie.SharedPreferencesCookieJar;
import com.fanwe.lib.http.interceptor.RequestInterceptor;
import com.fanwe.lib.http.utils.TransmitParam;
import com.fanwe.www.http.model.InitActModel;
import com.google.gson.Gson;

import java.io.File;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = "MainActivity";

    public static final String URL_FILE = "http://110.81.153.150/mmgr.myapp.com/msoft/sec/secure/GodDresser/1/2/3/102027/tencentmobilemanager_20170911185709_7.3.0_android_build4155_102027.apk?mkey=59db0a2773ba1ece&f=d273&c=0&p=.apk";
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
        Request.post(URL)
                .param("ctl", "app")
                .param("act", "init")
                .setTag(this)
                .execute(mModelRequestCallback_0, mModelRequestCallback_1);

        File file = new File(getExternalCacheDir(), "download.apk");
        Request.get(URL_FILE).setTag(this).execute(new FileRequestCallback(file)
        {
            @Override
            protected void onProgress(TransmitParam param)
            {
                Log.i(TAG, param.toString());
            }

            @Override
            public void onSuccess()
            {

            }

            @Override
            public void onError(Exception e)
            {
                super.onError(e);
                Log.i(TAG, "download error:" + e);
            }

            @Override
            public void onCancel()
            {
                super.onCancel();
                Log.i(TAG, "download cancelled");
            }
        });
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
            InitActModel model = getModel();
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
            InitActModel model = getModel();
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
        RequestManager.getInstance().cancelTag(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        RequestManager.getInstance().cancelTag(this);
        RequestManager.getInstance().removeRequestInterceptor(mRequestInterceptor);
    }
}

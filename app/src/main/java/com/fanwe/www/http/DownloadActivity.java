package com.fanwe.www.http;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fanwe.lib.http.Request;
import com.fanwe.lib.http.RequestManager;
import com.fanwe.lib.http.callback.FileRequestCallback;
import com.fanwe.lib.http.utils.TransmitParam;

import java.io.File;

public class DownloadActivity extends AppCompatActivity
{
    public static final String TAG = "DownloadActivity";

    public static final String URL_FILE = "http://110.81.153.150/mmgr.myapp.com/msoft/sec/secure/GodDresser/1/2/3/102027/tencentmobilemanager_20170911185709_7.3.0_android_build4155_102027.apk?mkey=59db0a2773ba1ece&f=d273&c=0&p=.apk";

    private ProgressBar mProgressBar;
    private TextView mTvProgress, mTvSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTvProgress = (TextView) findViewById(R.id.tv_progress);
        mTvSpeed = (TextView) findViewById(R.id.tv_speed);
    }

    public void onClickRequest(View view)
    {
        File file = new File(getExternalCacheDir(), "download.apk");
        Request.get(URL_FILE).setTag(this).execute(new FileRequestCallback(file)
        {
            @Override
            protected void onProgressDownload(TransmitParam param)
            {
                mProgressBar.setProgress(param.getProgress());
                mTvProgress.setText(param.getProgress() + "%");
                mTvSpeed.setText(param.getSpeedKBps() + "KB/秒");
                if (param.isFinish())
                {
                    mTvSpeed.setText("");
                    Log.i(TAG, "download finish");
                }
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
                mTvSpeed.setText("");
                Log.i(TAG, "download cancelled");
            }
        });
    }

    public void onClickCancelRequest(View view)
    {
        RequestManager.getInstance().cancelTag(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        RequestManager.getInstance().cancelTag(this);
    }
}

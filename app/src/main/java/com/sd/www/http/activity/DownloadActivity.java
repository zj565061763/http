package com.sd.www.http.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sd.lib.http.Request;
import com.sd.lib.http.RequestManager;
import com.sd.lib.http.callback.FileRequestCallback;
import com.sd.lib.http.impl.GetRequest;
import com.sd.lib.http.utils.TransmitParam;
import com.sd.www.http.R;

import java.io.File;

public class DownloadActivity extends AppCompatActivity
{
    public static final String TAG = DownloadActivity.class.getSimpleName();
    public static final String URL_FILE = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk";

    private ProgressBar mProgressBar;
    private TextView mTvProgress, mTvSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        mProgressBar = findViewById(R.id.progressBar);
        mTvProgress = findViewById(R.id.tv_progress);
        mTvSpeed = findViewById(R.id.tv_speed);
    }

    public void onClickRequest(View view)
    {
        final File file = new File(getExternalCacheDir(), "download.apk");

        final Request request = new GetRequest();
        request.setBaseUrl(URL_FILE);
        request.setTag(TAG);
        request.execute(new FileRequestCallback(file)
        {
            @Override
            protected void onProgressDownload(TransmitParam param)
            {
                mProgressBar.setProgress(param.getProgress());
                mTvProgress.setText(param.getProgress() + "%");
                mTvSpeed.setText(param.getSpeedKBps() + "KB/秒");
            }

            @Override
            public void onSuccess()
            {
                Log.i(TAG, "download finish");
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
        RequestManager.getInstance().cancelTag(TAG);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        RequestManager.getInstance().cancelTag(TAG);
    }
}

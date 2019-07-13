package com.sd.www.http.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sd.lib.http.IRequest;
import com.sd.lib.http.IResponse;
import com.sd.lib.http.impl.GetRequest;
import com.sd.www.http.R;

/**
 * 同步请求demo
 */
public class SyncRequestActivity extends AppCompatActivity
{
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_request);
        mTextView = findViewById(R.id.tv_result);
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
                    IRequest request = new GetRequest();
                    //设置请求地址
                    request.setBaseUrl("https://www.baidu.com/");
                    //发起请求，得到Response对象
                    IResponse response = request.execute();
                    //请求结果以字符串返回
                    final String result = response.getAsString();

                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mTextView.setText(result);
                        }
                    });
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

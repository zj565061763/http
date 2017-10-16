package com.fanwe.lib.http;

import android.os.CountDownTimer;

import com.fanwe.lib.http.callback.IRequestCallback;
import com.fanwe.lib.http.callback.UploadProgressCallback;
import com.fanwe.lib.http.utils.LogUtils;
import com.fanwe.lib.http.utils.TransmitParam;
import com.fanwe.lib.task.SDTask;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhengjun on 2017/10/11.
 */
public abstract class Request
{
    private String mUrl;

    private Map<String, Object> mMapParam;
    private Map<String, String> mMapHeader;

    private Object mTag;

    private int mReadTimeout;
    private int mConnectTimeout;

    private UploadProgressCallback mUploadProgressCallback;
    private TransmitParam mTransmitParam;
    private CountDownTimer mCountDownTimer;

    public Request(String url)
    {
        setUrl(url);
    }

    /**
     * 设置请求的url
     *
     * @param url
     * @return
     */
    public Request setUrl(String url)
    {
        mUrl = url;
        return this;
    }

    /**
     * 设置请求参数
     *
     * @param name
     * @param value
     * @return
     */
    public Request param(String name, Object value)
    {
        if (value != null)
        {
            getMapParam().put(name, value);

        } else
        {
            getMapParam().remove(name);
        }
        return this;
    }

    /**
     * 设置header参数
     *
     * @param name
     * @param value
     * @return
     */
    public Request header(String name, String value)
    {
        if (value != null)
        {
            getMapHeader().put(name, value);
        } else
        {
            getMapHeader().remove(name);
        }
        return this;
    }

    /**
     * 设置请求对应的tag
     *
     * @param tag
     * @return
     */
    public Request setTag(Object tag)
    {
        mTag = tag;
        return this;
    }

    public Request setReadTimeout(int readTimeout)
    {
        mReadTimeout = readTimeout;
        return this;
    }

    public Request setConnectTimeout(int connectTimeout)
    {
        mConnectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout()
    {
        return mReadTimeout;
    }

    public int getConnectTimeout()
    {
        return mConnectTimeout;
    }

    public Request setUploadProgressCallback(UploadProgressCallback uploadProgressCallback)
    {
        mUploadProgressCallback = uploadProgressCallback;
        return this;
    }

    public UploadProgressCallback getUploadProgressCallback()
    {
        if (mUploadProgressCallback == null)
        {
            mUploadProgressCallback = UploadProgressCallback.EMPTY_PROGRESS_CALLBACK;
        }
        return mUploadProgressCallback;
    }

    public TransmitParam getTransmitParam()
    {
        if (mTransmitParam == null)
        {
            mTransmitParam = new TransmitParam();
        }
        return mTransmitParam;
    }

    public Object getTag()
    {
        return mTag;
    }

    public String getUrl()
    {
        return mUrl;
    }

    public Map<String, Object> getMapParam()
    {
        if (mMapParam == null)
        {
            mMapParam = new LinkedHashMap<>();
        }
        return mMapParam;
    }

    public Map<String, String> getMapHeader()
    {
        if (mMapHeader == null)
        {
            mMapHeader = new LinkedHashMap<>();
        }
        return mMapHeader;
    }

    protected void notifyProgressUpload(long uploaded, long total)
    {
        LogUtils.i("progress upload:" + uploaded + "," + total);
        startCountDownTimer();
        getTransmitParam().transmit(uploaded, total);
        if (getTransmitParam().isFinish())
        {
            stopCountDownTimer();
            SDTask.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    //可能造成内存泄漏
                    getUploadProgressCallback().onProgressUpload(getTransmitParam());
                }
            });
        }
    }

    private void startCountDownTimer()
    {
        SDTask.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCountDownTimer == null)
                {
                    mCountDownTimer = new CountDownTimer(Long.MAX_VALUE, 1000)
                    {
                        @Override
                        public void onTick(long millisUntilFinished)
                        {
                            getUploadProgressCallback().onProgressUpload(getTransmitParam());
                        }

                        @Override
                        public void onFinish()
                        {
                        }
                    };
                    mCountDownTimer.start();
                }
            }
        });
    }

    private void stopCountDownTimer()
    {
        SDTask.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (mCountDownTimer != null)
                {
                    mCountDownTimer.cancel();
                    mCountDownTimer = null;
                }
            }
        });
    }

    /**
     * 异步请求
     *
     * @param callbacks
     */
    public final void execute(IRequestCallback... callbacks)
    {
        RequestManager.getInstance().execute(this, callbacks);
    }

    /**
     * 同步请求
     *
     * @return
     * @throws Exception
     */
    public final Response execute() throws Exception
    {
        RequestManager.getInstance().beforeExecute(this);

        Response response = new Response();
        response.setRequest(this);
        doExecute(response);

        RequestManager.getInstance().afterExecute(response);
        return response;
    }

    /**
     * 发起请求，并将请求结果填充到response
     *
     * @param response
     * @throws Exception
     */
    protected abstract void doExecute(Response response) throws Exception;
}

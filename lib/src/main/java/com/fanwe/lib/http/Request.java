package com.fanwe.lib.http;

import android.os.CountDownTimer;

import com.fanwe.lib.http.callback.IRequestCallback;
import com.fanwe.lib.http.callback.IUploadProgressCallback;
import com.fanwe.lib.http.utils.LogUtil;
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

    private IUploadProgressCallback mUploadProgressCallback;
    private TransmitParam mTransmitParam;
    private CountDownTimer mTimer;

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
     * 把mapParam中的请求参数都设置进去
     *
     * @param mapParam
     * @return
     */
    public Request param(Map<String, Object> mapParam)
    {
        if (mapParam != null)
        {
            getMapParam().putAll(mapParam);
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
     * 把mapHeader中的请求参数都设置进去
     *
     * @param mapHeader
     * @return
     */
    public Request header(Map<String, String> mapHeader)
    {
        if (mapHeader != null)
        {
            getMapHeader().putAll(mapHeader);
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

    public Request setUploadProgressCallback(IUploadProgressCallback uploadProgressCallback)
    {
        mUploadProgressCallback = uploadProgressCallback;
        return this;
    }

    public IUploadProgressCallback getUploadProgressCallback()
    {
        if (mUploadProgressCallback == null)
        {
            mUploadProgressCallback = IUploadProgressCallback.DEFAULT;
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
        LogUtil.i("progress upload:" + uploaded + "," + total);
        startTimer();
        getTransmitParam().transmit(uploaded, total);
        if (getTransmitParam().isFinish())
        {
            stopTimer();
            SDTask.runOnUiThread(mUpdateProgressRunnable); //可能内存泄漏？
        }
    }

    private synchronized void startTimer()
    {
        if (mTimer != null)
        {
            return;
        }

        SDTask.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                synchronized (Request.this)
                {
                    if (mTimer == null)
                    {
                        mTimer = new CountDownTimer(Long.MAX_VALUE, 1000)
                        {
                            @Override
                            public void onTick(long millisUntilFinished)
                            {
                                mUpdateProgressRunnable.run();
                            }

                            @Override
                            public void onFinish()
                            {
                            }
                        };
                        mTimer.start();
                    }
                }
            }
        });
    }

    private synchronized void stopTimer()
    {
        if (mTimer != null)
        {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private Runnable mUpdateProgressRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            getUploadProgressCallback().onProgressUpload(getTransmitParam());
        }
    };

    /**
     * 异步请求
     *
     * @param callback
     */
    public final RequestHandler execute(IRequestCallback callback)
    {
        return RequestManager.getInstance().execute(this, callback);
    }

    /**
     * 同步请求
     *
     * @return
     * @throws Exception
     */
    public final Response execute() throws Exception
    {
        RequestManager.getInstance().mRequestInterceptor.beforeExecute(this);

        Response response = new Response();
        doExecute(response);

        RequestManager.getInstance().mRequestInterceptor.afterExecute(this, response);
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

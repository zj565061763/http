package com.fanwe.lib.http;

import com.fanwe.lib.http.callback.IUploadProgressCallback;
import com.fanwe.lib.http.callback.RequestCallback;
import com.fanwe.lib.http.utils.TransmitParam;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhengjun on 2017/10/11.
 */
public abstract class Request implements IRequest
{
    private String mUrl;

    private final Map<String, String> mParams = new LinkedHashMap<>();
    private final Map<String, String> mHeaders = new LinkedHashMap<>();

    private String mTag;

    private int mConnectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int mReadTimeout = DEFAULT_READ_TIMEOUT;

    private IUploadProgressCallback mUploadProgressCallback;
    private TransmitParam mTransmitParam;

    //---------- IRequest implements start ----------

    @Override
    public final IRequest setUrl(String url)
    {
        mUrl = url;
        return this;
    }

    @Override
    public final IRequest param(String key, String value)
    {
        if (value != null)
        {
            mParams.put(key, value);
        } else
        {
            mParams.remove(key);
        }
        return this;
    }

    @Override
    public final IRequest header(String name, String value)
    {
        if (value != null)
        {
            mHeaders.put(name, value);
        } else
        {
            mHeaders.remove(name);
        }
        return this;
    }

    @Override
    public final IRequest setTag(String tag)
    {
        mTag = tag;
        return this;
    }

    @Override
    public final IRequest setConnectTimeout(int connectTimeout)
    {
        mConnectTimeout = connectTimeout;
        return this;
    }

    @Override
    public final IRequest setReadTimeout(int readTimeout)
    {
        mReadTimeout = readTimeout;
        return this;
    }

    @Override
    public final IRequest setUploadProgressCallback(IUploadProgressCallback callback)
    {
        mUploadProgressCallback = callback;
        return this;
    }

    @Override
    public final String getUrl()
    {
        return mUrl;
    }

    @Override
    public final String getParam(String key)
    {
        final Object value = mParams.get(key);
        if (value == null)
        {
            return null;
        } else
        {
            return String.valueOf(value);
        }
    }

    @Override
    public String getHeader(String key)
    {
        final Object value = mHeaders.get(key);
        if (value == null)
        {
            return null;
        } else
        {
            return String.valueOf(value);
        }
    }

    @Override
    public final String getTag()
    {
        return mTag;
    }

    @Override
    public final RequestHandler execute(RequestCallback callback)
    {
        return RequestManager.getInstance().execute(this, callback);
    }

    @Override
    public final RequestHandler executeSequence(RequestCallback callback)
    {
        return RequestManager.getInstance().execute(this, true, callback);
    }

    @Override
    public final IResponse execute() throws Exception
    {
        IResponse response = null;
        try
        {
            RequestManager.getInstance().mInternalRequestInterceptor.beforeExecute(this);
            response = doExecute();
        } finally
        {
            RequestManager.getInstance().mInternalRequestInterceptor.afterExecute(this, response);
        }
        return response;
    }

    //---------- IRequest implements end ----------

    /**
     * 发起请求
     *
     * @return
     * @throws Exception
     */
    protected abstract IResponse doExecute() throws Exception;

    protected final int getReadTimeout()
    {
        return mReadTimeout;
    }

    protected final int getConnectTimeout()
    {
        return mConnectTimeout;
    }

    protected final Map<String, String> getParams()
    {
        return mParams;
    }

    protected final Map<String, String> getHeaders()
    {
        return mHeaders;
    }

    protected final void notifyProgressUpload(long uploaded, long total)
    {
        if (mUploadProgressCallback != null)
        {
            if (mTransmitParam == null)
            {
                mTransmitParam = new TransmitParam();
            }
            mTransmitParam.transmit(uploaded, total);
            mUploadProgressCallback.onProgressUpload(mTransmitParam);
        }
    }
}

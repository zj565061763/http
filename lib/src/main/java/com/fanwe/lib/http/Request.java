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

    private Map<String, Object> mMapParam;
    private Map<String, String> mMapHeader;

    private String mTag;

    private int mConnectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int mReadTimeout = DEFAULT_READ_TIMEOUT;

    private IUploadProgressCallback mUploadProgressCallback;
    private TransmitParam mTransmitParam;

    public Request(String url)
    {
        setUrl(url);
    }

    //---------- IRequest implements start ----------

    @Override
    public final IRequest setUrl(String url)
    {
        mUrl = url;
        return this;
    }

    @Override
    public final IRequest param(String name, Object value)
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

    @Override
    public final IRequest param(Map<String, Object> mapParam)
    {
        if (mapParam != null)
        {
            getMapParam().putAll(mapParam);
        }
        return this;
    }

    @Override
    public final IRequest header(String name, String value)
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

    @Override
    public final IRequest header(Map<String, String> mapHeader)
    {
        if (mapHeader != null)
        {
            getMapHeader().putAll(mapHeader);
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
    public final Response execute() throws Exception
    {
        Response response = new Response();
        try
        {
            RequestManager.getInstance().mInternalRequestInterceptor.beforeExecute(this);
            doExecute(response);
        } finally
        {
            RequestManager.getInstance().mInternalRequestInterceptor.afterExecute(this, response);
        }
        return response;
    }

    //---------- IRequest implements end ----------

    /**
     * 发起请求，并将请求结果填充到response
     *
     * @param response
     * @throws Exception
     */
    protected abstract void doExecute(Response response) throws Exception;

    protected final int getReadTimeout()
    {
        return mReadTimeout;
    }

    protected final int getConnectTimeout()
    {
        return mConnectTimeout;
    }

    protected final Map<String, Object> getMapParam()
    {
        if (mMapParam == null)
        {
            mMapParam = new LinkedHashMap<>();
        }
        return mMapParam;
    }

    protected final Map<String, String> getMapHeader()
    {
        if (mMapHeader == null)
        {
            mMapHeader = new LinkedHashMap<>();
        }
        return mMapHeader;
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

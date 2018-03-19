package com.fanwe.lib.http;

import com.fanwe.lib.http.callback.IUploadProgressCallback;
import com.fanwe.lib.http.callback.RequestCallback;
import com.fanwe.lib.http.utils.HttpDataHolder;
import com.fanwe.lib.http.utils.TransmitParam;

/**
 * Created by zhengjun on 2017/10/11.
 */
public abstract class Request implements IRequest
{
    private String mUrl;
    private String mPath;

    private final HttpDataHolder<String, Object> mParams = new HttpDataHolder<>();
    private final HttpDataHolder<String, String> mHeaders = new HttpDataHolder<>();

    private String mTag;

    private int mConnectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int mReadTimeout = DEFAULT_READ_TIMEOUT;

    private IUploadProgressCallback mUploadProgressCallback;
    private TransmitParam mTransmitParam;

    protected Request()
    {
        final String baseUrl = RequestManager.getInstance().getBaseUrl();
        setUrl(baseUrl);
    }

    //---------- IRequest implements start ----------

    @Override
    public final IRequest setUrl(String url)
    {
        mUrl = url;
        return this;
    }

    @Override
    public final IRequest setPath(String path)
    {
        mPath = path;
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
    public final HttpDataHolder<String, Object> getParams()
    {
        return mParams;
    }

    @Override
    public final HttpDataHolder<String, String> getHeaders()
    {
        return mHeaders;
    }

    @Override
    public final String getUrl()
    {
        return mUrl + getPath();
    }

    public final String getPath()
    {
        if (mPath == null)
        {
            mPath = "";
        }
        return mPath;
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

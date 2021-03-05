package com.sd.lib.http;

import com.sd.lib.http.callback.IUploadProgressCallback;
import com.sd.lib.http.callback.RequestCallback;
import com.sd.lib.http.utils.HttpDataHolder;
import com.sd.lib.http.utils.TransmitParam;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

public abstract class Request implements IRequest
{
    private String mBaseUrl;
    private String mUrlSuffix;

    private final HttpDataHolder<String, Object> mParams = new HttpDataHolder<>();
    private final HttpDataHolder<String, String> mHeaders = new HttpDataHolder<>();

    private String mTag;

    private int mConnectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private int mReadTimeout = DEFAULT_READ_TIMEOUT;

    private SSLSocketFactory mSSLSocketFactory;
    private HostnameVerifier mHostnameVerifier;

    private IUploadProgressCallback mUploadProgressCallback;
    private TransmitParam mTransmitParam;

    private boolean mInterceptExecute = true;

    //---------- IRequest implements start ----------

    @Override
    public final void setBaseUrl(String baseUrl)
    {
        mBaseUrl = baseUrl;
    }

    @Override
    public final void setUrlSuffix(String urlSuffix)
    {
        mUrlSuffix = urlSuffix;
    }

    @Override
    public final void setTag(String tag)
    {
        mTag = tag;
    }

    @Override
    public final void setConnectTimeout(int connectTimeout)
    {
        mConnectTimeout = connectTimeout;
    }

    @Override
    public final void setReadTimeout(int readTimeout)
    {
        mReadTimeout = readTimeout;
    }

    @Override
    public final void setUploadProgressCallback(IUploadProgressCallback callback)
    {
        mUploadProgressCallback = callback;
    }

    @Override
    public final void setSSLSocketFactory(SSLSocketFactory sslSocketFactory)
    {
        mSSLSocketFactory = sslSocketFactory;
    }

    public final void setHostnameVerifier(HostnameVerifier hostnameVerifier)
    {
        mHostnameVerifier = hostnameVerifier;
    }

    @Override
    public final void setInterceptExecute(boolean interceptExecute)
    {
        mInterceptExecute = interceptExecute;
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
    public final String getBaseUrl()
    {
        if (mBaseUrl == null)
            mBaseUrl = "";
        return mBaseUrl;
    }

    @Override
    public final String getUrlSuffix()
    {
        if (mUrlSuffix == null)
            mUrlSuffix = "";
        return mUrlSuffix;
    }

    @Override
    public String getUrl()
    {
        return getBaseUrl() + getUrlSuffix();
    }

    @Override
    public final String getTag()
    {
        return mTag;
    }

    @Override
    public final SSLSocketFactory getSSLSocketFactory()
    {
        return mSSLSocketFactory;
    }

    @Override
    public final HostnameVerifier getHostnameVerifier()
    {
        return mHostnameVerifier;
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
        final boolean intercept = mInterceptExecute;

        if (intercept)
        {
            try
            {
                final IResponse beforeResponse = RequestManager.getInstance().mInternalRequestInterceptor.beforeExecute(this);
                if (beforeResponse != null)
                    return beforeResponse;
            } catch (Exception e)
            {
                RequestManager.getInstance().mInternalRequestInterceptor.onError(e);
            }
        }

        final IResponse realResponse = doExecute();

        if (intercept)
        {
            try
            {
                final IResponse afterResponse = RequestManager.getInstance().mInternalRequestInterceptor.afterExecute(this, realResponse);
                if (afterResponse != null)
                    return afterResponse;
            } catch (Exception e)
            {
                RequestManager.getInstance().mInternalRequestInterceptor.onError(e);
            }
        }

        return realResponse;
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
        if (mUploadProgressCallback == null)
            return;

        if (mTransmitParam == null)
            mTransmitParam = new TransmitParam();

        if (mTransmitParam.transmit(total, uploaded))
        {
            final TransmitParam param = mTransmitParam.copy();
            FTask.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mUploadProgressCallback != null)
                        mUploadProgressCallback.onProgressUpload(param);
                }
            });
        }
    }
}

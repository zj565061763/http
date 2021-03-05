package com.sd.lib.http.impl;

import android.text.TextUtils;

import com.sd.lib.http.IResponse;
import com.sd.lib.http.Request;
import com.sd.lib.http.security.SSLSocketFactoryProvider;
import com.sd.lib.http.utils.HttpIOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public abstract class BaseRequestImpl extends Request
{
    private static SSLSocketFactory TRUSTED_FACTORY;

    protected HttpRequest newHttpRequest(String url, String method) throws KeyManagementException, NoSuchAlgorithmException
    {
        final FHttpRequest request = new FHttpRequest(url, method);
        request.headers(getHeaders().toMap());
        request.readTimeout(getReadTimeout());
        request.connectTimeout(getConnectTimeout());
        request.progress(new HttpRequest.UploadProgress()
        {
            @Override
            public void onUpload(long uploaded, long total)
            {
                notifyProgressUpload(uploaded, total);
            }
        });

        final HttpURLConnection connection = request.getConnection();
        if (connection instanceof HttpsURLConnection)
        {
            SSLSocketFactory sslSocketFactory = getSSLSocketFactory();
            if (sslSocketFactory == null)
                sslSocketFactory = getTrustedFactory();

            final HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
            httpsConnection.setSSLSocketFactory(sslSocketFactory);

            final HostnameVerifier hostnameVerifier = getHostnameVerifier();
            if (hostnameVerifier != null)
                httpsConnection.setHostnameVerifier(hostnameVerifier);
        }

        return request;
    }

    private static synchronized SSLSocketFactory getTrustedFactory() throws KeyManagementException, NoSuchAlgorithmException
    {
        if (TRUSTED_FACTORY == null)
        {
            synchronized (BaseRequestImpl.class)
            {
                if (TRUSTED_FACTORY == null)
                    TRUSTED_FACTORY = SSLSocketFactoryProvider.getTrustedFactory();
            }
        }
        return TRUSTED_FACTORY;
    }

    @Override
    public String toString()
    {
        final String url = HttpRequest.append(getUrl(), getParams().toMap());
        return url + " " + super.toString();
    }

    public static class Response implements IResponse
    {
        private HttpRequest mHttpRequest;
        private String mBody;

        public Response(HttpRequest httpRequest)
        {
            mHttpRequest = httpRequest;
        }

        @Override
        public int getCode()
        {
            return mHttpRequest.code();
        }

        public int getCodeOrThrow() throws Exception
        {
            try
            {
                return getCode();
            } catch (HttpRequest.HttpRequestException e)
            {
                throw e.getCause();
            }
        }

        @Override
        public int getContentLength()
        {
            return mHttpRequest.contentLength();
        }

        @Override
        public Map<String, List<String>> getHeaders()
        {
            return mHttpRequest.headers();
        }

        @Override
        public String getCharset()
        {
            return mHttpRequest.charset();
        }

        @Override
        public InputStream getInputStream()
        {
            return mHttpRequest.stream();
        }

        @Override
        public synchronized String getAsString() throws IOException
        {
            if (TextUtils.isEmpty(mBody))
            {
                try
                {
                    mBody = HttpIOUtil.readString(getInputStream(), getCharset());
                } finally
                {
                    HttpIOUtil.closeQuietly(getInputStream());
                }
            }
            return mBody;
        }
    }
}

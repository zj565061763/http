package com.fanwe.lib.http;

/**
 * Created by zhengjun on 2017/10/11.
 */

abstract class HttpRequestImpl extends Request
{
    protected HttpRequestImpl(String url)
    {
        super(url);
    }

    protected HttpRequest newHttpRequest(String url, String method)
    {
        SDHttpRequest request = new SDHttpRequest(url, method);
        request.headers(getMapHeader());
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
        return request;
    }

    @Override
    public String toString()
    {
        String url = HttpRequest.append(getUrl(), getMapParam());
        return url;
    }
}

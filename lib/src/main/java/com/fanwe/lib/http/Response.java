package com.fanwe.lib.http;

import android.text.TextUtils;

import com.fanwe.lib.http.utils.IOUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhengjun on 2017/10/11.
 */
public class Response
{
    private Request request;

    private int code;
    private int contentLength;
    private String charset;
    private InputStream inputStream;

    void setRequest(Request request)
    {
        this.request = request;
    }

    void fillValue(HttpRequest request)
    {
        if (request == null)
        {
            return;
        }
        setCode(request.code());
        setContentLength(request.contentLength());

        String charset = request.charset();
        if (TextUtils.isEmpty(charset))
        {
            charset = "UTF-8";
        }
        setCharset(charset);

        setInputStream(request.stream());
    }

    /**
     * 将请求结果转为字符串，此方法必须在非ui线程中执行
     *
     * @return
     * @throws IOException
     */
    public String parseToString() throws IOException
    {
        return new String(IOUtil.readBytes(getInputStream()), getCharset());
    }

    public Request getRequest()
    {
        return request;
    }

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
    }

    public int getContentLength()
    {
        return contentLength;
    }

    public void setContentLength(int contentLength)
    {
        this.contentLength = contentLength;
    }

    public String getCharset()
    {
        return charset;
    }

    public void setCharset(String charset)
    {
        this.charset = charset;
    }

    public InputStream getInputStream()
    {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }
}

package com.fanwe.lib.http;

import android.text.TextUtils;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Created by zhengjun on 2017/10/10.
 */
class SDHttpRequest extends HttpRequest
{
    public SDHttpRequest(CharSequence url, String method) throws HttpRequestException
    {
        super(url, method);
        loadCookieForRequest();
    }

    public SDHttpRequest(URL url, String method) throws HttpRequestException
    {
        super(url, method);
        loadCookieForRequest();
    }

    /**
     * 'Set-Cookie' header name
     */
    public static final String HEADER_SET_COOKIE = "Set-Cookie";
    /**
     * 'Set-Cookie2' header name
     */
    public static final String HEADER_SET_COOKIE2 = "Set-Cookie2";
    /**
     * 'Cookie' header name
     */
    public static final String HEADER_COOKIE = "Cookie";

    private int mCode;

    public SDHttpRequest addRequestCookie(HttpCookie httpCookie)
    {
        if (httpCookie != null)
        {
            String cookie = header(HEADER_COOKIE);
            if (TextUtils.isEmpty(cookie))
            {
                cookie = httpCookie.toString();
            } else
            {
                cookie += ";" + httpCookie.toString();
            }
            header(HEADER_COOKIE, cookie);
        }
        return this;
    }

    public SDHttpRequest setRequestCookie(List<HttpCookie> listCookie)
    {
        if (listCookie != null && !listCookie.isEmpty())
        {
            StringBuilder sb = new StringBuilder();
            for (HttpCookie item : listCookie)
            {
                sb.append(item.toString()).append(";");
            }
            sb.deleteCharAt(sb.lastIndexOf(";"));
            header(HEADER_COOKIE, sb.toString());
        }
        return this;
    }

    public List<HttpCookie> getResponseCookie()
    {
        String cookie = header(HEADER_SET_COOKIE);
        if (TextUtils.isEmpty(cookie))
        {
            cookie = header(HEADER_SET_COOKIE2);
        }
        if (!TextUtils.isEmpty(cookie))
        {
            List<HttpCookie> listCookie = HttpCookie.parse(cookie);
            return listCookie;
        }

        return null;
    }

    private void loadCookieForRequest()
    {
        try
        {
            URI uri = url().toURI();
            List<HttpCookie> listRequest = RequestManager.getInstance().getCookieStore().get(uri);
            setRequestCookie(listRequest);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void saveCookieFromResponse()
    {
        try
        {
            List<HttpCookie> listResponse = getResponseCookie();
            if (listResponse != null)
            {
                URI uri = url().toURI();
                for (HttpCookie item : listResponse)
                {
                    RequestManager.getInstance().getCookieStore().add(uri, item);
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setCode(int code)
    {
        if (mCode == 0 && code != 0)
        {
            saveCookieFromResponse();
            mCode = code;
        }
    }

    //---------- Override start ----------

    @Override
    public int code() throws HttpRequestException
    {
        int code = super.code();
        setCode(code);
        return code;
    }

    //---------- Override end ----------
}

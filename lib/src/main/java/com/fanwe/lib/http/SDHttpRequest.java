package com.fanwe.lib.http;

import android.text.TextUtils;

import com.fanwe.lib.http.utils.LogUtil;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            String cookie = TextUtils.join(";", listCookie);
            header(HEADER_COOKIE, cookie);

            LogUtil.i("cookie loadCookieFor " + url() + "\r\n" + cookie);
        }
        return this;
    }

    public List<HttpCookie> getResponseCookie()
    {
        Map<String, List<String>> mapHeaders = headers();
        if (mapHeaders != null && !mapHeaders.isEmpty())
        {
            List<String> listCookie = mapHeaders.get(HEADER_SET_COOKIE);
            if (listCookie == null || listCookie.isEmpty())
            {
                listCookie = mapHeaders.get(HEADER_SET_COOKIE2);
            }
            if (listCookie != null && !listCookie.isEmpty())
            {
                LogUtil.i("cookie ---------->saveCookieFrom " + url() + "\r\n" + TextUtils.join("\r\n", listCookie));

                List<HttpCookie> listResult = new ArrayList<>();
                for (String item : listCookie)
                {
                    listResult.addAll(HttpCookie.parse(item));
                }
                return listResult;
            }
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
        } catch (URISyntaxException e)
        {
            LogUtil.e("cookie loadCookieForRequest error:" + e);
        }
    }

    private void saveCookieFromResponse()
    {
        try
        {
            URI uri = url().toURI();
            List<HttpCookie> listResponse = getResponseCookie();
            RequestManager.getInstance().getCookieStore().add(uri, listResponse);
        } catch (URISyntaxException e)
        {
            LogUtil.e("cookie saveCookieFromResponse error:" + e);
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

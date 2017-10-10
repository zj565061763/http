package com.fanwe.lib.http.cookie;

import java.net.HttpCookie;
import java.net.URL;
import java.util.List;

/**
 * Created by zhengjun on 2017/10/10.
 */
public interface CookieJar
{
    CookieJar EMPTY_COOKIE_JAR = new CookieJar()
    {
        @Override
        public void saveFromResponse(URL url, List<HttpCookie> listCookie)
        {
        }

        @Override
        public List<HttpCookie> loadForRequest(URL url)
        {
            return null;
        }
    };

    void saveFromResponse(URL url, List<HttpCookie> listCookie);

    List<HttpCookie> loadForRequest(URL url);
}

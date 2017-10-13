package com.fanwe.lib.http.cookie;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * Created by zhengjun on 2017/10/13.
 */
public interface PersistableCookieStore extends CookieStore
{
    void save();

    void add(URI uri, List<HttpCookie> listCookie);
}

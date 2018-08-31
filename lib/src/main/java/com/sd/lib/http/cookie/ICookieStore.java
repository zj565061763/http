package com.sd.lib.http.cookie;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public interface ICookieStore extends CookieStore
{
    ICookieStore DEFAULT = new ModifyMemoryCookieStore();

    void add(URI uri, List<HttpCookie> listCookie);
}

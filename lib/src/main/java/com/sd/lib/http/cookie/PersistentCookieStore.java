package com.sd.lib.http.cookie;

import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * 可持久化的CookieStore
 */
public abstract class PersistentCookieStore implements ICookieStore
{
    protected abstract ModifyMemoryCookieStore getMemoryCookieStore();

    protected abstract void save();

    @Override
    public void add(URI uri, List<HttpCookie> listCookie)
    {
        if (listCookie != null && !listCookie.isEmpty())
        {
            getMemoryCookieStore().add(uri, listCookie);
            save();
        }
    }

    @Override
    public void add(URI uri, HttpCookie cookie)
    {
        getMemoryCookieStore().add(uri, cookie);
        save();
    }

    @Override
    public List<HttpCookie> get(URI uri)
    {
        return getMemoryCookieStore().get(uri);
    }

    @Override
    public List<HttpCookie> getCookies()
    {
        return getMemoryCookieStore().getCookies();
    }

    @Override
    public List<URI> getURIs()
    {
        return getMemoryCookieStore().getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie)
    {
        final boolean result = getMemoryCookieStore().remove(uri, cookie);
        if (result)
            save();

        return result;
    }

    @Override
    public boolean removeAll()
    {
        final boolean result = getMemoryCookieStore().removeAll();
        if (result)
            save();

        return result;
    }
}

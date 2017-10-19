package com.fanwe.lib.http.cookie;

import com.fanwe.lib.http.utils.LogUtil;

import java.io.Serializable;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengjun on 2017/10/13.
 */
class MemoryCookieStore implements ICookieStore, Serializable
{
    static final long serialVersionUID = 0L;

    private Map<String, List<CookieModel>> mMapCookie = new HashMap<>();

    private String getUriString(URI uri)
    {
        return uri.getHost();
    }

    private List<CookieModel> getListCookie(URI uri)
    {
        final String uriString = getUriString(uri);
        List<CookieModel> listCookie = mMapCookie.get(uriString);
        if (listCookie == null)
        {
            listCookie = new ArrayList<>();
            mMapCookie.put(uriString, listCookie);
        }
        return listCookie;
    }

    @Override
    public synchronized void add(URI uri, HttpCookie cookie)
    {
        List<CookieModel> listCookie = getListCookie(uri);

        CookieModel target = null;
        Iterator<CookieModel> it = listCookie.iterator();
        while (it.hasNext())
        {
            CookieModel item = it.next();
            if (item.isExpiry())
            {
                it.remove();
            } else
            {
                if (item.getName().equals(cookie.getName()))
                {
                    item.fillValue(uri, cookie);

                    target = item;
                    break;
                }
            }
        }

        if (target == null)
        {
            target = new CookieModel();
            target.fillValue(uri, cookie);
            listCookie.add(target);
        }

        LogUtil.i("cookie add cookie:" + uri.toString() + "\r\n" + target.toString());
    }

    @Override
    public synchronized List<HttpCookie> get(URI uri)
    {
        List<HttpCookie> listHttpCookie = new ArrayList<>();

        List<CookieModel> listCookie = getListCookie(uri);
        Iterator<CookieModel> it = listCookie.iterator();
        while (it.hasNext())
        {
            CookieModel item = it.next();
            if (item.isExpiry())
            {
                it.remove();
            } else
            {
                HttpCookie httpCookie = item.toHttpCookie();
                listHttpCookie.add(httpCookie);
            }
        }
        return listHttpCookie;
    }

    @Override
    public synchronized List<HttpCookie> getCookies()
    {
        List<HttpCookie> listHttpCookie = new ArrayList<>();
        if (!mMapCookie.isEmpty())
        {
            Iterator<List<CookieModel>> itMap = mMapCookie.values().iterator();
            while (itMap.hasNext())
            {
                List<CookieModel> itemMap = itMap.next();
                Iterator<CookieModel> it = itemMap.iterator();
                while (it.hasNext())
                {
                    CookieModel item = it.next();
                    if (item.isExpiry())
                    {
                        it.remove();
                    } else
                    {
                        HttpCookie httpCookie = item.toHttpCookie();
                        listHttpCookie.add(httpCookie);
                    }
                }
            }
        }
        return listHttpCookie;
    }

    @Override
    public synchronized List<URI> getURIs()
    {
        return null;
    }

    @Override
    public synchronized boolean remove(URI uri, HttpCookie cookie)
    {
        List<CookieModel> listCookie = getListCookie(uri);
        Iterator<CookieModel> it = listCookie.iterator();
        while (it.hasNext())
        {
            CookieModel item = it.next();
            if (item.isExpiry())
            {
                it.remove();
            } else
            {
                if (item.getName().equals(cookie.getName()))
                {
                    item.fillValue(uri, cookie);
                    it.remove();
                    LogUtil.i("cookie remove:" + uri.toString() + "\r\n" + item.toString());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public synchronized boolean removeAll()
    {
        if (mMapCookie.isEmpty())
        {
            return false;
        } else
        {
            mMapCookie.clear();
            LogUtil.i("cookie removeAll");
            return true;
        }
    }

    @Override
    public synchronized void add(URI uri, List<HttpCookie> listCookie)
    {
        if (listCookie != null)
        {
            for (HttpCookie item : listCookie)
            {
                add(uri, item);
            }
        }
    }
}

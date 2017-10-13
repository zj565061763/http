package com.fanwe.lib.http.cookie;

import android.content.Context;

import com.fanwe.lib.http.utils.IOUtil;

import java.io.File;
import java.io.Serializable;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * Created by zhengjun on 2017/10/13.
 */

public class SerializableCookieStore implements PersistableCookieStore, Serializable
{
    private Context mContext;
    private MemoryCookieStore mMemoryCookieStore;
    private File mFile;

    public SerializableCookieStore(Context context)
    {
        mContext = context.getApplicationContext();
    }

    private synchronized MemoryCookieStore getMemoryCookieStore()
    {
        if (mMemoryCookieStore == null)
        {
            try
            {
                mMemoryCookieStore = IOUtil.deserializeObject(getFile());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            if (mMemoryCookieStore == null)
            {
                mMemoryCookieStore = new MemoryCookieStore();
            }
        }
        return mMemoryCookieStore;
    }

    private File getFile()
    {
        if (mFile == null)
        {
            mFile = new File(mContext.getExternalCacheDir(), "httpcookie");
        }
        if (!mFile.exists())
        {
            try
            {
                mFile.createNewFile();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return mFile;
    }

    @Override
    public synchronized void save()
    {
        try
        {
            IOUtil.serializeObject(getMemoryCookieStore(), getFile());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void add(URI uri, List<HttpCookie> listCookie)
    {
        if (listCookie != null)
        {
            for (HttpCookie item : listCookie)
            {
                getMemoryCookieStore().add(uri, item);
            }
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
        boolean result = getMemoryCookieStore().remove(uri, cookie);
        if (result)
        {
            save();
        }
        return result;
    }

    @Override
    public boolean removeAll()
    {
        boolean result = getMemoryCookieStore().removeAll();
        if (result)
        {
            save();
        }
        return getMemoryCookieStore().removeAll();
    }
}

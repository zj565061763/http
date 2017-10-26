package com.fanwe.lib.http.cookie;

import android.content.Context;

import com.fanwe.lib.http.utils.IOUtil;
import com.fanwe.lib.http.utils.LogUtil;

import java.io.File;
import java.io.Serializable;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

/**
 * Created by zhengjun on 2017/10/13.
 */

public class SerializableCookieStore implements ICookieStore, Serializable
{
    private Context mContext;
    private ModifyMemoryCookieStore mMemoryCookieStore;
    private File mFile;

    public SerializableCookieStore(Context context)
    {
        mContext = context.getApplicationContext();
    }

    private synchronized ModifyMemoryCookieStore getMemoryCookieStore()
    {
        if (mMemoryCookieStore == null)
        {
            try
            {
                mMemoryCookieStore = IOUtil.deserializeObject(getFile());
            } catch (Exception e)
            {
                LogUtil.e("cookie deserialize cookiestore error:" + e);
            }
            if (mMemoryCookieStore == null)
            {
                mMemoryCookieStore = new ModifyMemoryCookieStore();
                LogUtil.i("cookie create MemoryCookieStore");
            } else
            {
                LogUtil.i("cookie deserialize cookiestore success");
            }
        }
        return mMemoryCookieStore;
    }

    private File getFile()
    {
        if (mFile == null)
        {
            mFile = new File(mContext.getFilesDir(), "httpcookie");
        }
        if (!mFile.exists())
        {
            try
            {
                mFile.createNewFile();
            } catch (Exception e)
            {
                LogUtil.e("cookie create httpcookie file error:" + e);
            }
        }
        return mFile;
    }

    private synchronized void save()
    {
        try
        {
            IOUtil.serializeObject(getMemoryCookieStore(), getFile());
            LogUtil.i("cookie save cookiestore success");
        } catch (Exception e)
        {
            LogUtil.e("cookie save cookiestore error:" + e);
        }
    }

    @Override
    public void add(URI uri, List<HttpCookie> listCookie)
    {
        if (listCookie != null)
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
        return result;
    }
}

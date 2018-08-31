package com.sd.lib.http.cookie;

import android.content.Context;

import com.sd.lib.http.utils.HttpIOUtil;
import com.sd.lib.http.utils.HttpLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 通过序列化和反序列化实现的可持久化CookieStore
 */
public class SerializableCookieStore extends PersistentCookieStore
{
    private Context mContext;
    private ModifyMemoryCookieStore mMemoryCookieStore;
    private File mFile;

    public SerializableCookieStore(Context context)
    {
        mContext = context.getApplicationContext();
    }

    @Override
    protected synchronized ModifyMemoryCookieStore getMemoryCookieStore()
    {
        if (mMemoryCookieStore == null)
        {
            try
            {
                mMemoryCookieStore = deserializeObject(getFile());
            } catch (Exception e)
            {
                HttpLog.e("cookie deserialize cookiestore error:" + e);
            }

            if (mMemoryCookieStore == null)
            {
                mMemoryCookieStore = new ModifyMemoryCookieStore();
                HttpLog.i("cookie create MemoryCookieStore");
            } else
            {
                HttpLog.i("cookie deserialize cookiestore success");
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
                HttpLog.e("cookie create httpcookie file error:" + e);
            }
        }
        return mFile;
    }

    @Override
    protected synchronized void save()
    {
        try
        {
            serializeObject(getMemoryCookieStore(), getFile());
            HttpLog.i("cookie save cookiestore success");
        } catch (Exception e)
        {
            HttpLog.e("cookie save cookiestore error:" + e);
        }
    }

    private static <T extends Serializable> void serializeObject(T object, File file) throws Exception
    {
        ObjectOutputStream oos = null;
        try
        {
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(object);
            oos.flush();
        } finally
        {
            HttpIOUtil.closeQuietly(oos);
        }
    }

    private static <T extends Serializable> T deserializeObject(File file) throws Exception
    {
        ObjectInputStream ois = null;
        try
        {
            ois = new ObjectInputStream(new FileInputStream(file));
            Object object = ois.readObject();
            return (T) object;
        } finally
        {
            HttpIOUtil.closeQuietly(ois);
        }
    }
}

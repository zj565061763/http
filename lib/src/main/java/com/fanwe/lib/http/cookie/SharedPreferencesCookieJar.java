package com.fanwe.lib.http.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.net.HttpCookie;
import java.net.URL;
import java.util.List;

/**
 * Created by zhengjun on 2017/10/10.
 */
public class SharedPreferencesCookieJar implements CookieJar
{
    public static final String TAG = "SharedPreferencesCookieJar";

    private Context mContext;

    public SharedPreferencesCookieJar(Context context)
    {
        mContext = context.getApplicationContext();
    }

    public SharedPreferences getSharedPreferences()
    {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public synchronized void saveFromResponse(URL url, List<HttpCookie> listCookie)
    {
        if (url == null)
        {
            return;
        }
        if (listCookie == null || listCookie.isEmpty())
        {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (HttpCookie item : listCookie)
        {
            sb.append(item.toString()).append(";");
        }
        sb.deleteCharAt(sb.lastIndexOf(";"));

        String host = url.getHost();
        String cookie = sb.toString();

        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(host, cookie);
        editor.commit();

        Log.i(TAG, "saveFromResponse:" + url + ":" + cookie);
    }

    @Override
    public synchronized List<HttpCookie> loadForRequest(URL url)
    {
        List<HttpCookie> lisCookie = null;

        String host = url.getHost();
        String cookie = null;

        if (getSharedPreferences().contains(host))
        {
            cookie = getSharedPreferences().getString(host, null);
            if (!TextUtils.isEmpty(cookie))
            {
                lisCookie = HttpCookie.parse(cookie);
            }
        }

        Log.i(TAG, "loadForRequest:" + url + ":" + cookie);
        return lisCookie;
    }
}

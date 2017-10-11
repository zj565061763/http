package com.fanwe.lib.http;

import com.fanwe.lib.http.cookie.CookieJar;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by zhengjun on 2017/10/11.
 */

public class RequestManager
{
    private static RequestManager sInstance;

    private CookieJar mCookieJar = CookieJar.EMPTY_COOKIE_JAR;
    private Map<RequestTask, Integer> mMapRequest = new WeakHashMap<>();

    private RequestManager()
    {
    }

    public static RequestManager getInstance()
    {
        if (sInstance == null)
        {
            synchronized (RequestManager.class)
            {
                if (sInstance == null)
                {
                    sInstance = new RequestManager();
                }
            }
        }
        return sInstance;
    }

    public void setCookieJar(CookieJar cookieJar)
    {
        if (cookieJar == null)
        {
            cookieJar = CookieJar.EMPTY_COOKIE_JAR;
        }
        mCookieJar = cookieJar;
    }

    public CookieJar getCookieJar()
    {
        return mCookieJar;
    }

    public void execute(Request request, RequestCallback callback)
    {
        if (request == null)
        {
            return;
        }

        RequestTask task = new RequestTask(request, callback);
        task.submit();
        mMapRequest.put(task, 0);
    }

    public void cancelRequest(Object tag)
    {
        if (mMapRequest.isEmpty() || tag == null)
        {
            return;
        }

        for (Map.Entry<RequestTask, Integer> item : mMapRequest.entrySet())
        {
            RequestTask task = item.getKey();
            if (tag.equals(task.getRequest().getTag()))
            {
                task.cancel();
            }
        }
    }
}

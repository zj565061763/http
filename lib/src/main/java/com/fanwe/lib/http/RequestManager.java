package com.fanwe.lib.http;

import com.fanwe.lib.http.cookie.CookieJar;
import com.fanwe.lib.http.interceptor.RequestInterceptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by zhengjun on 2017/10/11.
 */

public class RequestManager implements RequestInterceptor
{
    private static RequestManager sInstance;

    private CookieJar mCookieJar;
    private Map<RequestTask, Integer> mMapRequest = new WeakHashMap<>();
    private List<RequestInterceptor> mListRequestInterceptor;

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

    /**
     * 设置cookie管理对象
     *
     * @param cookieJar
     */
    public void setCookieJar(CookieJar cookieJar)
    {
        mCookieJar = cookieJar;
    }

    /**
     * 返回cookie管理对象
     *
     * @return
     */
    public CookieJar getCookieJar()
    {
        if (mCookieJar == null)
        {
            mCookieJar = CookieJar.EMPTY_COOKIE_JAR;
        }
        return mCookieJar;
    }

    /**
     * 异步执行请求
     *
     * @param request
     * @param callback
     */
    public synchronized void execute(Request request, RequestCallback callback)
    {
        if (request == null)
        {
            return;
        }

        RequestTask task = new RequestTask(request, callback);
        task.submit();

        mMapRequest.put(task, 0);
    }

    /**
     * 取消请求
     *
     * @param tag
     */
    public synchronized void cancelRequest(Object tag)
    {
        if (mMapRequest.isEmpty() || tag == null)
        {
            return;
        }

        Iterator<Map.Entry<RequestTask, Integer>> it = mMapRequest.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<RequestTask, Integer> item = it.next();
            RequestTask task = item.getKey();
            if (tag.equals(task.getRequest().getTag()))
            {
                task.cancel();
                it.remove();
            }
        }
    }

    private List<RequestInterceptor> getListRequestInterceptor()
    {
        if (mListRequestInterceptor == null)
        {
            mListRequestInterceptor = new ArrayList<>();
        }
        return mListRequestInterceptor;
    }

    public synchronized void addRequestInterceptor(RequestInterceptor interceptor)
    {
        if (interceptor == null)
        {
            return;
        }
        if (!getListRequestInterceptor().contains(interceptor))
        {
            getListRequestInterceptor().add(interceptor);
        }
    }

    public synchronized void removeRequestInterceptor(RequestInterceptor interceptor)
    {
        if (interceptor == null || mListRequestInterceptor == null)
        {
            return;
        }
        mListRequestInterceptor.remove(interceptor);
        if (mListRequestInterceptor.isEmpty())
        {
            mListRequestInterceptor = null;
        }
    }

    @Override
    public synchronized void beforeExecute(Request request)
    {
        if (mListRequestInterceptor == null)
        {
            return;
        }
        for (RequestInterceptor item : mListRequestInterceptor)
        {
            item.beforeExecute(request);
        }
    }

    @Override
    public synchronized void afterExecute(Request request, Response response)
    {
        if (mListRequestInterceptor == null)
        {
            return;
        }
        for (RequestInterceptor item : mListRequestInterceptor)
        {
            item.afterExecute(request, response);
        }
    }
}

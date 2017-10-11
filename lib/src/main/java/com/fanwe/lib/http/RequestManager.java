package com.fanwe.lib.http;

import com.fanwe.lib.http.cookie.CookieJar;
import com.fanwe.lib.http.interceptor.RequestInterceptor;

import java.util.ArrayList;
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
    private Map<RequestTask, Integer> mMapRequest;
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

    private Map<RequestTask, Integer> getMapRequest()
    {
        if (mMapRequest == null)
        {
            mMapRequest = new WeakHashMap<>();
        }
        return mMapRequest;
    }

    /**
     * 异步执行请求
     *
     * @param request
     * @param callback
     */
    public void execute(Request request, RequestCallback callback)
    {
        if (request == null)
        {
            return;
        }

        RequestTask task = new RequestTask(request, callback);
        task.submit();

        getMapRequest().put(task, 0);
    }

    /**
     * 取消请求
     *
     * @param tag
     */
    public void cancelRequest(Object tag)
    {
        if (mMapRequest == null || mMapRequest.isEmpty() || tag == null)
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

    private List<RequestInterceptor> getListRequestInterceptor()
    {
        if (mListRequestInterceptor == null)
        {
            mListRequestInterceptor = new ArrayList<>();
        }
        return mListRequestInterceptor;
    }

    public void addRequestInterceptor(RequestInterceptor interceptor)
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

    public void removeRequestInterceptor(RequestInterceptor interceptor)
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
    public synchronized void afterExcute(Request request, Response response)
    {
        if (mListRequestInterceptor == null)
        {
            return;
        }
        for (RequestInterceptor item : mListRequestInterceptor)
        {
            item.afterExcute(request, response);
        }
    }
}
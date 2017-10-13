package com.fanwe.lib.http;

import com.fanwe.lib.http.callback.IRequestCallback;
import com.fanwe.lib.http.callback.RequestCallbackProxy;
import com.fanwe.lib.http.cookie.CookieJar;
import com.fanwe.lib.http.interceptor.RequestInterceptor;
import com.fanwe.lib.task.SDTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengjun on 2017/10/11.
 */

public class RequestManager implements RequestInterceptor
{
    private static RequestManager sInstance;

    private CookieJar mCookieJar;
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
     * @param callbacks
     */
    public void execute(Request request, IRequestCallback... callbacks)
    {
        if (request == null)
        {
            return;
        }

        IRequestCallback realCallback = null;
        if (callbacks != null)
        {
            if (callbacks.length == 1)
            {
                realCallback = callbacks[0];
            } else if (callbacks.length > 1)
            {
                realCallback = RequestCallbackProxy.get(callbacks);
            }
        }

        RequestTask task = new RequestTask(request, realCallback);
        task.submit(request.getTag());
    }

    /**
     * 根据tag取消请求
     *
     * @param tag
     */
    public void cancelTag(Object tag)
    {
        SDTask.cancelTag(tag, true);
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
    public synchronized void afterExecute(Response response)
    {
        if (mListRequestInterceptor == null)
        {
            return;
        }
        for (RequestInterceptor item : mListRequestInterceptor)
        {
            item.afterExecute(response);
        }
    }
}

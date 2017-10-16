package com.fanwe.lib.http;

import com.fanwe.lib.http.callback.IRequestCallback;
import com.fanwe.lib.http.callback.RequestCallbackProxy;
import com.fanwe.lib.http.cookie.ICookieStore;
import com.fanwe.lib.http.interceptor.RequestInterceptor;

import java.net.HttpCookie;
import java.net.URI;
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

    private Map<RequestTask, Integer> mMapRequest = new WeakHashMap<>();

    private ICookieStore mCookieStore;
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
     * @param cookieStore
     */
    public void setCookieStore(ICookieStore cookieStore)
    {
        mCookieStore = cookieStore;
    }

    /**
     * 返回cookie管理对象
     *
     * @return
     */
    public ICookieStore getCookieStore()
    {
        if (mCookieStore == null)
        {
            mCookieStore = EMPTY_COOKIE_STORE;
        }
        return mCookieStore;
    }

    /**
     * 异步执行请求
     *
     * @param request
     * @param callbacks
     */
    public synchronized void execute(Request request, IRequestCallback... callbacks)
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
        if (realCallback == null)
        {
            realCallback = IRequestCallback.EMPTY_CALLBACK;
        }

        realCallback.onPrepare(request);
        RequestTask task = new RequestTask(request, realCallback);
        task.submit(null);
        mMapRequest.put(task, 0);
    }

    /**
     * 根据tag取消请求
     *
     * @param tag
     * @return 申请取消成功的数量
     */
    public synchronized int cancelTag(Object tag)
    {
        int count = 0;
        if (tag != null && !mMapRequest.isEmpty())
        {
            Iterator<Map.Entry<RequestTask, Integer>> it = mMapRequest.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry<RequestTask, Integer> item = it.next();
                RequestTask task = item.getKey();

                if (task.isDone())
                {
                    it.remove();
                } else
                {
                    if (tag.equals(task.getRequest().getTag()))
                    {
                        task.cancel(true);
                        it.remove();
                        count++;
                    }
                }
            }
        }
        return count;
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

    private static final ICookieStore EMPTY_COOKIE_STORE = new ICookieStore()
    {

        @Override
        public void add(URI uri, List<HttpCookie> listCookie)
        {
        }

        @Override
        public void add(URI uri, HttpCookie cookie)
        {
        }

        @Override
        public List<HttpCookie> get(URI uri)
        {
            return null;
        }

        @Override
        public List<HttpCookie> getCookies()
        {
            return null;
        }

        @Override
        public List<URI> getURIs()
        {
            return null;
        }

        @Override
        public boolean remove(URI uri, HttpCookie cookie)
        {
            return false;
        }

        @Override
        public boolean removeAll()
        {
            return false;
        }
    };
}

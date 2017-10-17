package com.fanwe.lib.http;

import android.text.TextUtils;

import com.fanwe.lib.http.callback.IRequestCallback;
import com.fanwe.lib.http.callback.RequestCallbackProxy;
import com.fanwe.lib.http.cookie.ICookieStore;
import com.fanwe.lib.http.interceptor.RequestInterceptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by zhengjun on 2017/10/11.
 */

public class RequestManager
{
    private static RequestManager sInstance;

    private Map<RequestTask, RequestInfo> mMapRequest = new WeakHashMap<>();

    private ICookieStore mCookieStore;
    private RequestIdentifierProvider mRequestIdentifierProvider;
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
            mCookieStore = ICookieStore.DEFAULT;
        }
        return mCookieStore;
    }

    public void setRequestIdentifierProvider(RequestIdentifierProvider requestIdentifierProvider)
    {
        mRequestIdentifierProvider = requestIdentifierProvider;
    }

    public RequestIdentifierProvider getRequestIdentifierProvider()
    {
        if (mRequestIdentifierProvider == null)
        {
            mRequestIdentifierProvider = RequestIdentifierProvider.DEFAULT;
        }
        return mRequestIdentifierProvider;
    }

    /**
     * 异步执行请求
     *
     * @param request
     * @param callbacks
     */
    public synchronized RequestHandler execute(Request request, IRequestCallback... callbacks)
    {
        if (request == null)
        {
            return null;
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
            realCallback = IRequestCallback.DEFAULT;
        }

        realCallback.onPrepare(request);
        RequestTask task = new RequestTask(request, realCallback);
        task.submit(null);

        RequestInfo info = new RequestInfo();
        info.setTag(request.getTag());
        info.setRequestIdentifier(getRequestIdentifierProvider().provideRequestIdentifier(request));

        mMapRequest.put(task, info);
        return new RequestHandler(task);
    }

    /**
     * 根据tag取消请求
     *
     * @param tag
     * @return 申请取消成功的数量
     */
    public synchronized int cancelTag(Object tag)
    {
        if (tag == null || mMapRequest.isEmpty())
        {
            return 0;
        }

        int count = 0;
        Iterator<Map.Entry<RequestTask, RequestInfo>> it = mMapRequest.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<RequestTask, RequestInfo> item = it.next();

            RequestTask task = item.getKey();
            RequestInfo info = item.getValue();

            if (task.isDone())
            {
                it.remove();
            } else
            {
                if (tag.equals(info.getTag()))
                {
                    task.cancel(true);
                    it.remove();
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 根据Request的唯一标识取消请求{@link RequestIdentifierProvider}
     *
     * @param request
     * @return
     */
    public synchronized int cancelRequestIdentifier(Request request)
    {
        if (request == null || mMapRequest.isEmpty())
        {
            return 0;
        }
        String identifier = getRequestIdentifierProvider().provideRequestIdentifier(request);
        if (TextUtils.isEmpty(identifier))
        {
            return 0;
        }

        int count = 0;
        Iterator<Map.Entry<RequestTask, RequestInfo>> it = mMapRequest.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<RequestTask, RequestInfo> item = it.next();

            RequestTask task = item.getKey();
            RequestInfo info = item.getValue();

            if (task.isDone())
            {
                it.remove();
            } else
            {
                if (identifier.equals(info.getRequestIdentifier()))
                {
                    task.cancel(true);
                    it.remove();
                    count++;
                }
            }
        }
        return count;
    }

    //---------- RequestInterceptor start ----------

    private List<RequestInterceptor> getListRequestInterceptor()
    {
        if (mListRequestInterceptor == null)
        {
            mListRequestInterceptor = new ArrayList<>();
        }
        return mListRequestInterceptor;
    }

    /**
     * 添加请求拦截对象
     *
     * @param interceptor
     */
    public void addRequestInterceptor(RequestInterceptor interceptor)
    {
        synchronized (mRequestInterceptor)
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
    }

    /**
     * 移除请求拦截对象
     *
     * @param interceptor
     */
    public void removeRequestInterceptor(RequestInterceptor interceptor)
    {
        synchronized (mRequestInterceptor)
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
    }

    RequestInterceptor mRequestInterceptor = new RequestInterceptor()
    {
        @Override
        public void beforeExecute(Request request)
        {
            synchronized (this)
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
        }

        @Override
        public void afterExecute(Response response)
        {
            synchronized (this)
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
    };

    //---------- RequestInterceptor end ----------

}

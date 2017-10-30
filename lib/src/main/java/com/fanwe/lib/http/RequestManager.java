package com.fanwe.lib.http;

import android.text.TextUtils;

import com.fanwe.lib.http.callback.IRequestCallback;
import com.fanwe.lib.http.cookie.ICookieStore;
import com.fanwe.lib.http.interceptor.IRequestInterceptor;

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
    private IRequestIdentifierProvider mRequestIdentifierProvider;
    private List<IRequestInterceptor> mListRequestInterceptor;

    private boolean isDebug = false;

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

    public void setDebug(boolean debug)
    {
        isDebug = debug;
    }

    public boolean isDebug()
    {
        return isDebug;
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

    public void setRequestIdentifierProvider(IRequestIdentifierProvider requestIdentifierProvider)
    {
        mRequestIdentifierProvider = requestIdentifierProvider;
    }

    public IRequestIdentifierProvider getRequestIdentifierProvider()
    {
        if (mRequestIdentifierProvider == null)
        {
            mRequestIdentifierProvider = IRequestIdentifierProvider.DEFAULT;
        }
        return mRequestIdentifierProvider;
    }

    /**
     * 异步执行请求
     *
     * @param request
     * @param callback
     */
    public synchronized RequestHandler execute(Request request, IRequestCallback callback)
    {
        if (request == null)
        {
            return null;
        }

        if (callback == null)
        {
            callback = IRequestCallback.DEFAULT;
        }

        callback.onPrepare(request);
        RequestTask task = new RequestTask(request, callback);
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
    public synchronized int cancelTag(String tag)
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
     * 根据Request的唯一标识取消请求{@link IRequestIdentifierProvider}
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

    //---------- IRequestInterceptor start ----------

    private List<IRequestInterceptor> getListRequestInterceptor()
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
    public synchronized void addRequestInterceptor(IRequestInterceptor interceptor)
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

    /**
     * 移除请求拦截对象
     *
     * @param interceptor
     */
    public synchronized void removeRequestInterceptor(IRequestInterceptor interceptor)
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

    IRequestInterceptor mRequestInterceptor = new IRequestInterceptor()
    {
        @Override
        public void beforeExecute(Request request)
        {
            synchronized (RequestManager.this)
            {
                if (mListRequestInterceptor == null)
                {
                    return;
                }
                for (IRequestInterceptor item : mListRequestInterceptor)
                {
                    item.beforeExecute(request);
                }
            }
        }

        @Override
        public void afterExecute(Request request, Response response)
        {
            synchronized (RequestManager.this)
            {
                if (mListRequestInterceptor == null)
                {
                    return;
                }
                for (IRequestInterceptor item : mListRequestInterceptor)
                {
                    item.afterExecute(request, response);
                }
            }
        }
    };

    //---------- IRequestInterceptor end ----------

}

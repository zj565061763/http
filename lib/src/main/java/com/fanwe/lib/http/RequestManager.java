package com.fanwe.lib.http;

import android.text.TextUtils;

import com.fanwe.lib.http.callback.RequestCallback;
import com.fanwe.lib.http.cookie.ICookieStore;
import com.fanwe.lib.http.interceptor.IRequestInterceptor;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by zhengjun on 2017/10/11.
 */

public class RequestManager
{
    private static RequestManager sInstance;

    private String mBaseUrl;
    private Map<RequestTask, RequestInfo> mMapRequest = new WeakHashMap<>();

    private ICookieStore mCookieStore;
    private IRequestIdentifierProvider mRequestIdentifierProvider;
    private List<IRequestInterceptor> mListRequestInterceptor = new CopyOnWriteArrayList<>();

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
     * 设置基础请求url
     *
     * @param baseUrl
     */
    public void setBaseUrl(String baseUrl)
    {
        mBaseUrl = baseUrl;
    }

    /**
     * 返回设置的基础请求url
     *
     * @return
     */
    public String getBaseUrl()
    {
        return mBaseUrl;
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

    /**
     * 设置Request标识生成对象
     *
     * @param requestIdentifierProvider
     */
    public void setRequestIdentifierProvider(IRequestIdentifierProvider requestIdentifierProvider)
    {
        mRequestIdentifierProvider = requestIdentifierProvider;
    }

    private IRequestIdentifierProvider getRequestIdentifierProvider()
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
     * @param request  请求对象
     * @param callback
     */
    public RequestHandler execute(IRequest request, RequestCallback callback)
    {
        return execute(request, false, callback);
    }

    /**
     * 异步执行请求
     *
     * @param request  请求对象
     * @param sequence 是否按顺序一个个执行，true-是
     * @param callback
     * @return
     */
    public synchronized RequestHandler execute(final IRequest request, final boolean sequence, RequestCallback callback)
    {
        if (request == null)
        {
            return null;
        }

        if (callback == null)
        {
            callback = new RequestCallback()
            {
                @Override
                public void onSuccess()
                {
                }
            };
        }

        callback.setRequest(request);
        callback.onPrepare(request);

        final RequestTask task = new RequestTask(request, callback);
        if (sequence)
        {
            task.submitSequence();
        } else
        {
            task.submit();
        }

        final RequestInfo info = new RequestInfo();
        info.tag = request.getTag();
        info.requestIdentifier = getRequestIdentifierProvider().provideRequestIdentifier(request);

        mMapRequest.put(task, info);
        return new RequestHandler(task);
    }

    /**
     * 根据tag取消请求
     *
     * @param tag
     * @return 申请取消成功的数量
     */
    public synchronized int cancelTag(final String tag)
    {
        if (tag == null || mMapRequest.isEmpty())
        {
            return 0;
        }

        int count = 0;
        final Iterator<Map.Entry<RequestTask, RequestInfo>> it = mMapRequest.entrySet().iterator();
        while (it.hasNext())
        {
            final Map.Entry<RequestTask, RequestInfo> item = it.next();
            final RequestTask task = item.getKey();
            final RequestInfo info = item.getValue();

            if (task.isDone())
            {
                it.remove();
            } else
            {
                if (tag.equals(info.tag) && task.cancel(true))
                {
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
    public synchronized int cancelRequestIdentifier(final IRequest request)
    {
        if (request == null || mMapRequest.isEmpty())
        {
            return 0;
        }
        final String identifier = getRequestIdentifierProvider().provideRequestIdentifier(request);
        if (TextUtils.isEmpty(identifier))
        {
            return 0;
        }

        int count = 0;
        final Iterator<Map.Entry<RequestTask, RequestInfo>> it = mMapRequest.entrySet().iterator();
        while (it.hasNext())
        {
            final Map.Entry<RequestTask, RequestInfo> item = it.next();
            final RequestTask task = item.getKey();
            final RequestInfo info = item.getValue();

            if (task.isDone())
            {
                it.remove();
            } else
            {
                if (identifier.equals(info.requestIdentifier) && task.cancel(true))
                {
                    it.remove();
                    count++;
                }
            }
        }
        return count;
    }

    //---------- IRequestInterceptor start ----------

    /**
     * 添加请求拦截对象
     *
     * @param interceptor
     */
    public void addRequestInterceptor(IRequestInterceptor interceptor)
    {
        if (interceptor == null || mListRequestInterceptor.contains(interceptor))
        {
            return;
        }
        mListRequestInterceptor.add(interceptor);
    }

    /**
     * 移除请求拦截对象
     *
     * @param interceptor
     */
    public void removeRequestInterceptor(IRequestInterceptor interceptor)
    {
        mListRequestInterceptor.remove(interceptor);
    }

    IRequestInterceptor mInternalRequestInterceptor = new IRequestInterceptor()
    {
        @Override
        public void beforeExecute(IRequest request)
        {
            for (IRequestInterceptor item : mListRequestInterceptor)
            {
                item.beforeExecute(request);
            }
        }

        @Override
        public void afterExecute(IRequest request, IResponse response)
        {
            for (IRequestInterceptor item : mListRequestInterceptor)
            {
                item.afterExecute(request, response);
            }
        }
    };

    //---------- IRequestInterceptor end ----------

    private class RequestInfo
    {
        public String tag;
        public String requestIdentifier;
    }
}

package com.sd.lib.http;

import android.text.TextUtils;
import android.util.Log;

import com.sd.lib.http.callback.RequestCallback;
import com.sd.lib.http.cookie.ICookieStore;
import com.sd.lib.http.interceptor.IRequestInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestManager
{
    private static RequestManager sInstance;

    private final Map<RequestTask, RequestInfo> mMapRequest = new ConcurrentHashMap<>();

    private ICookieStore mCookieStore;

    private IRequestInterceptor mRequestInterceptor;
    private IRequestIdentifierProvider mRequestIdentifierProvider;

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
                    sInstance = new RequestManager();
            }
        }
        return sInstance;
    }

    public void setDebug(boolean debug)
    {
        isDebug = debug;
        FTaskManager.getInstance().setDebug(debug);
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
            mCookieStore = ICookieStore.DEFAULT;
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
            mRequestIdentifierProvider = new IRequestIdentifierProvider()
            {
                @Override
                public String provideRequestIdentifier(IRequest request)
                {
                    return null;
                }
            };
        }
        return mRequestIdentifierProvider;
    }

    //---------- IRequestInterceptor start ----------

    public void setRequestInterceptor(IRequestInterceptor requestInterceptor)
    {
        mRequestInterceptor = requestInterceptor;
    }

    final IRequestInterceptor mInternalRequestInterceptor = new IRequestInterceptor()
    {
        @Override
        public IResponse beforeExecute(IRequest request) throws Exception
        {
            if (mRequestInterceptor != null)
                return mRequestInterceptor.beforeExecute(request);

            return null;
        }

        @Override
        public IResponse afterExecute(IRequest request, IResponse response) throws Exception
        {
            if (mRequestInterceptor != null)
                return mRequestInterceptor.afterExecute(request, response);

            return response;
        }

        @Override
        public void onError(final Exception e)
        {
            if (mRequestInterceptor != null)
            {
                mRequestInterceptor.onError(e);
            } else
            {
                FTask.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    };

    //---------- IRequestInterceptor end ----------

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
            return null;

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

        final RequestTask task = new RequestTask(request, callback, new RequestTask.Callback()
        {
            @Override
            public void onFinish(RequestTask task)
            {
                removeTask(task);
            }
        });

        final String tag = request.getTag();
        final String requestIdentifier = getRequestIdentifierProvider().provideRequestIdentifier(request);

        final RequestInfo info = new RequestInfo();
        info.tag = tag;
        info.requestIdentifier = requestIdentifier;
        mMapRequest.put(task, info);

        if (isDebug())
        {
            Log.i(RequestManager.class.getName(), "execute"
                    + " task:" + task
                    + " callback:" + callback
                    + " requestIdentifier:" + requestIdentifier
                    + " tag:" + tag
                    + " size:" + mMapRequest.size());
        }

        if (sequence)
        {
            task.submitSequence();
        } else
        {
            task.submit();
        }

        return new RequestHandler(task);
    }

    private synchronized boolean removeTask(RequestTask task)
    {
        if (task == null)
            throw new IllegalArgumentException("task is null");

        final RequestInfo info = mMapRequest.remove(task);
        if (info == null)
            return false;

        if (isDebug())
        {
            Log.i(RequestManager.class.getName(), "removeTask"
                    + " task:" + task
                    + " size:" + mMapRequest.size());
        }

        return true;
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
            return 0;

        if (isDebug())
            Log.i(RequestManager.class.getName(), "try cancelTag tag:" + tag);

        int count = 0;
        for (Map.Entry<RequestTask, RequestInfo> item : mMapRequest.entrySet())
        {
            final RequestTask task = item.getKey();
            final RequestInfo info = item.getValue();

            if (tag.equals(info.tag) && task.cancel(true))
                count++;
        }

        if (isDebug())
            Log.i(RequestManager.class.getName(), "try cancelTag tag:" + tag + " count:" + count);

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
            return 0;

        final String identifier = getRequestIdentifierProvider().provideRequestIdentifier(request);
        if (TextUtils.isEmpty(identifier))
            return 0;

        if (isDebug())
            Log.i(RequestManager.class.getName(), "try cancelRequestIdentifier requestIdentifier:" + identifier);

        int count = 0;
        for (Map.Entry<RequestTask, RequestInfo> item : mMapRequest.entrySet())
        {
            final RequestTask task = item.getKey();
            final RequestInfo info = item.getValue();

            if (identifier.equals(info.requestIdentifier) && task.cancel(true))
                count++;
        }

        if (isDebug())
            Log.i(RequestManager.class.getName(), "try cancelRequestIdentifier requestIdentifier:" + identifier + " count:" + count);

        return count;
    }


    private static final class RequestInfo
    {
        public String tag;
        public String requestIdentifier;
    }
}

package com.sd.lib.http;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

class FTaskManager
{
    private static FTaskManager sInstance;

    private final ExecutorService DEFAULT_EXECUTOR = Executors.newCachedThreadPool();
    private final ExecutorService SINGLE_EXECUTOR = Executors.newSingleThreadExecutor();

    private final Map<Runnable, FTaskInfo> mMapTaskInfo = new HashMap<>();
    private final Map<String, Map<FTaskInfo, String>> mMapTaskTag = new HashMap<>();

    private boolean mDebug;

    private FTaskManager()
    {
    }

    public static FTaskManager getInstance()
    {
        if (sInstance == null)
        {
            synchronized (FTaskManager.class)
            {
                if (sInstance == null)
                    sInstance = new FTaskManager();
            }
        }
        return sInstance;
    }

    public boolean isDebug()
    {
        return mDebug;
    }

    public void setDebug(boolean debug)
    {
        mDebug = debug;
    }

    public FTaskInfo submit(Runnable runnable)
    {
        return submit(runnable, null);
    }

    public FTaskInfo submit(Runnable runnable, TaskCallback callback)
    {
        return submit(runnable, null, callback);
    }

    public FTaskInfo submit(Runnable runnable, String tag, TaskCallback callback)
    {
        return submitTo(runnable, tag, DEFAULT_EXECUTOR, callback);
    }

    public FTaskInfo submitSequence(Runnable runnable)
    {
        return submitSequence(runnable, null);
    }

    public FTaskInfo submitSequence(Runnable runnable, TaskCallback callback)
    {
        return submitSequence(runnable, null, callback);
    }

    public FTaskInfo submitSequence(Runnable runnable, String tag, TaskCallback callback)
    {
        return submitTo(runnable, tag, SINGLE_EXECUTOR, callback);
    }

    /**
     * 提交要执行的Runnable
     *
     * @param runnable        要执行的Runnable
     * @param executorService 要执行Runnable的线程池
     * @param tag             对应的tag，可用于取消
     * @param callback        任务执行回调
     * @return
     */
    public synchronized FTaskInfo submitTo(Runnable runnable, String tag, ExecutorService executorService, TaskCallback callback)
    {
        if (tag == null)
            tag = "";

        cancel(runnable, true);

        final RunnableWrapper wrapper = new RunnableWrapper(runnable, callback);
        final FTaskInfo info = new FTaskInfo(tag, wrapper);
        mMapTaskInfo.put(runnable, info);

        Map<FTaskInfo, String> mapTagInfo = mMapTaskTag.get(tag);
        if (mapTagInfo == null)
        {
            mapTagInfo = new ConcurrentHashMap<>();
            mMapTaskTag.put(tag, mapTagInfo);
        }
        mapTagInfo.put(info, "");

        if (isDebug())
        {
            Log.i(FTaskManager.class.getName(), "+++++ submitTo runnable:" + runnable + " tag:" + tag + " callback:" + callback + "\r\n" +
                    "size:" + mMapTaskInfo.size() + "," + mMapTaskTag.size() + "-" + mapTagInfo.size());
        }

        executorService.submit(wrapper);

        return info;
    }

    /**
     * 返回Runnable对应的任务信息
     *
     * @param runnable
     * @return
     */
    public synchronized FTaskInfo getTaskInfo(Runnable runnable)
    {
        return mMapTaskInfo.get(runnable);
    }

    /**
     * 返回tag对应的任务信息列表
     *
     * @param tag
     * @return
     */
    public synchronized List<FTaskInfo> getTaskInfo(String tag)
    {
        final List<FTaskInfo> listInfo = new ArrayList<>();

        if (!TextUtils.isEmpty(tag) && mMapTaskTag.size() > 0)
        {
            final Map<FTaskInfo, String> map = mMapTaskTag.get(tag);
            if (map != null && map.size() > 0)
            {
                listInfo.addAll(map.keySet());
            }
        }

        return listInfo;
    }

    /**
     * 取消Runnable
     *
     * @param runnable
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return true-申请取消成功
     */
    public synchronized boolean cancel(Runnable runnable, boolean mayInterruptIfRunning)
    {
        final FTaskInfo info = getTaskInfo(runnable);
        if (info == null)
            return false;

        if (isDebug())
            Log.i(FTaskManager.class.getName(), "try cancel runnable:" + runnable + " mayInterruptIfRunning:" + mayInterruptIfRunning);

        final boolean result = info.cancel(mayInterruptIfRunning);

        if (isDebug())
            Log.i(FTaskManager.class.getName(), "try cancel runnable:" + runnable + " result:" + result);

        return result;
    }

    /**
     * 根据tag取消Runnable
     *
     * @param tag
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return 申请取消成功的数量
     */
    public synchronized int cancelTag(String tag, boolean mayInterruptIfRunning)
    {
        int count = 0;

        if (isDebug())
            Log.i(FTaskManager.class.getName(), "try cancelTag tag:" + tag + " mayInterruptIfRunning:" + mayInterruptIfRunning);

        final List<FTaskInfo> listInfo = getTaskInfo(tag);
        for (FTaskInfo item : listInfo)
        {
            if (item.cancel(mayInterruptIfRunning))
                count++;
        }

        if (isDebug())
            Log.i(FTaskManager.class.getName(), "try cancelTag tag:" + tag + " count:" + count);

        return count;
    }

    private synchronized boolean removeTask(Runnable runnable)
    {
        if (runnable == null)
            throw new IllegalArgumentException("runnable is null");

        final FTaskInfo info = mMapTaskInfo.remove(runnable);
        if (info != null)
        {
            final String tag = info.getTag();
            final Map<FTaskInfo, String> mapTagInfo = mMapTaskTag.get(tag);

            if (mapTagInfo != null)
            {
                final boolean result = mapTagInfo.remove(info) != null;
                if (mapTagInfo.isEmpty())
                    mMapTaskTag.remove(tag);

                if (isDebug())
                {
                    Log.i(FTaskManager.class.getName(), "removeTask runnable:" + runnable + " result:" + result + "\r\n" +
                            "size:" + mMapTaskInfo.size() + "," + mMapTaskTag.size() + "-" + mapTagInfo.size());
                }

                return result;
            }
        }

        return false;
    }

    private final class RunnableWrapper extends FutureTask<String>
    {
        private final Runnable mRunnable;
        private final TaskCallback mCallback;

        public RunnableWrapper(Runnable runnable, TaskCallback callback)
        {
            super(new CallableRunnable(runnable));
            mRunnable = runnable;
            mCallback = callback;
        }

        @Override
        protected void done()
        {
            super.done();

            if (isDebug())
                Log.i(FTaskManager.class.getName(), "----- done runnable:" + mRunnable);

            final boolean remove = removeTask(mRunnable);
            if (!remove)
                throw new RuntimeException("remove task error, runnable was not found:" + mRunnable);

            try
            {
                get();
            } catch (InterruptedException e)
            {
                onError(e);
            } catch (CancellationException e)
            {
                onCancel();
            } catch (ExecutionException e)
            {
                onError(e.getCause());
            }

            onFinish();
        }

        protected void onError(Throwable throwable)
        {
            if (isDebug())
                Log.i(FTaskManager.class.getName(), "done onError:" + throwable + " runnable:" + mRunnable);

            if (mCallback != null)
                mCallback.onError(throwable);
        }

        protected void onCancel()
        {
            if (isDebug())
                Log.i(FTaskManager.class.getName(), "done onCancel:" + mRunnable);

            if (mCallback != null)
                mCallback.onCancel();
        }

        protected void onFinish()
        {
            if (isDebug())
                Log.i(FTaskManager.class.getName(), "done onFinish:" + mRunnable);

            if (mCallback != null)
                mCallback.onFinish();
        }
    }

    private final class CallableRunnable implements Callable<String>
    {
        private final Runnable mRunnable;

        public CallableRunnable(Runnable runnable)
        {
            if (runnable == null)
                throw new IllegalArgumentException("runnable is null");
            mRunnable = runnable;
        }

        @Override
        public String call() throws Exception
        {
            if (isDebug())
                Log.i(FTaskManager.class.getName(), "call runnable:" + mRunnable);

            mRunnable.run();
            return null;
        }
    }

    public interface TaskCallback
    {
        void onError(Throwable e);

        void onCancel();

        void onFinish();
    }
}

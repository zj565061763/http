package com.sd.lib.http;

import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

class FTaskManager
{
    private static FTaskManager sInstance;

    private final ExecutorService DEFAULT_EXECUTOR = Executors.newCachedThreadPool();
    private final ExecutorService SINGLE_EXECUTOR = Executors.newSingleThreadExecutor();

    private final Map<TaskRunnable, FTaskInfo> mMapTaskInfo = new HashMap<>();
    private final Map<String, Set<FTaskInfo>> mMapTaskTag = new HashMap<>();

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

    public FTaskInfo submit(TaskRunnable runnable)
    {
        return submit(runnable, null);
    }

    public FTaskInfo submit(TaskRunnable runnable, String tag)
    {
        return submitTo(runnable, tag, DEFAULT_EXECUTOR);
    }

    public FTaskInfo submitSequence(TaskRunnable runnable)
    {
        return submitSequence(runnable, null);
    }

    public FTaskInfo submitSequence(TaskRunnable runnable, String tag)
    {
        return submitTo(runnable, tag, SINGLE_EXECUTOR);
    }

    /**
     * 提交要执行的Runnable
     *
     * @param runnable        要执行的Runnable
     * @param executorService 要执行Runnable的线程池
     * @param tag             对应的tag，可用于取消
     * @return
     */
    public synchronized FTaskInfo submitTo(TaskRunnable runnable, String tag, ExecutorService executorService)
    {
        if (tag == null)
            tag = "";

        cancel(runnable, true);

        final RunnableWrapper wrapper = new RunnableWrapper(runnable);
        final FTaskInfo info = new FTaskInfo(tag, wrapper);
        mMapTaskInfo.put(runnable, info);

        Set<FTaskInfo> tagInfo = mMapTaskTag.get(tag);
        if (tagInfo == null)
        {
            tagInfo = new HashSet<>();
            mMapTaskTag.put(tag, tagInfo);
        }
        tagInfo.add(info);

        if (isDebug())
        {
            Log.i(FTaskManager.class.getName(), "+++++ submitTo runnable:" + runnable + "\r\n" +
                    "size:" + mMapTaskInfo.size() + "," + mMapTaskTag.size() + "," + tagInfo.size() + " - " + tag);
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
    public synchronized FTaskInfo getTaskInfo(TaskRunnable runnable)
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
        if (TextUtils.isEmpty(tag))
            return null;

        if (mMapTaskTag.isEmpty())
            return null;

        final Set<FTaskInfo> tagInfo = mMapTaskTag.get(tag);
        if (tagInfo == null || tagInfo.isEmpty())
            return null;

        return new ArrayList<>(tagInfo);
    }

    /**
     * 取消Runnable
     *
     * @param runnable
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return true-申请取消成功
     */
    public synchronized boolean cancel(TaskRunnable runnable, boolean mayInterruptIfRunning)
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
        final List<FTaskInfo> listInfo = getTaskInfo(tag);
        if (listInfo == null)
            return 0;

        if (isDebug())
            Log.i(FTaskManager.class.getName(), "try cancelTag tag:" + tag + " mayInterruptIfRunning:" + mayInterruptIfRunning);

        int count = 0;
        for (FTaskInfo item : listInfo)
        {
            if (item.cancel(mayInterruptIfRunning))
                count++;
        }

        if (isDebug())
            Log.i(FTaskManager.class.getName(), "try cancelTag tag:" + tag + " count:" + count);

        return count;
    }

    private synchronized boolean removeTask(TaskRunnable runnable)
    {
        if (runnable == null)
            throw new IllegalArgumentException("runnable is null");

        final FTaskInfo info = mMapTaskInfo.remove(runnable);
        if (info == null)
            return false;

        final String tag = info.getTag();
        final Set<FTaskInfo> tagInfo = mMapTaskTag.get(tag);
        if (tagInfo == null)
            return false;

        final boolean result = tagInfo.remove(info);
        if (tagInfo.isEmpty())
            mMapTaskTag.remove(tag);

        if (isDebug())
        {
            Log.i(FTaskManager.class.getName(), "removeTask runnable:" + runnable + " result:" + result + "\r\n" +
                    "size:" + mMapTaskInfo.size() + "," + mMapTaskTag.size() + "," + tagInfo.size() + " - " + tag);
        }

        return result;
    }

    private final class RunnableWrapper extends FutureTask<String>
    {
        private final TaskRunnable mRunnable;

        public RunnableWrapper(TaskRunnable runnable)
        {
            super(new CallableRunnable(runnable));
            mRunnable = runnable;
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
                final Throwable cause = e.getCause();
                if (cause instanceof Exception)
                    onError((Exception) cause);
                else
                    onError(e);
            } finally
            {
                onFinish();
            }
        }

        protected void onError(Exception e)
        {
            if (isDebug())
                Log.i(FTaskManager.class.getName(), "done onError:" + e + " runnable:" + mRunnable);

            mRunnable.onError(e);
        }

        protected void onCancel()
        {
            if (isDebug())
                Log.i(FTaskManager.class.getName(), "done onCancel:" + mRunnable);

            mRunnable.onCancel();
        }

        protected void onFinish()
        {
            if (isDebug())
                Log.i(FTaskManager.class.getName(), "done onFinish:" + mRunnable);

            mRunnable.onFinish();
        }
    }

    private final class CallableRunnable implements Callable<String>
    {
        private final TaskRunnable mRunnable;

        public CallableRunnable(TaskRunnable runnable)
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

            mRunnable.onRun();
            return null;
        }
    }

    public interface TaskRunnable
    {
        void onRun() throws Exception;

        void onError(Exception e);

        void onCancel();

        void onFinish();
    }
}

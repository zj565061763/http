package com.sd.lib.http;

import java.util.concurrent.Future;

class FTaskInfo
{
    private final String mTag;
    private final Future mFuture;

    FTaskInfo(String tag, Future future)
    {
        if (tag == null || future == null)
            throw new IllegalArgumentException();

        mTag = tag;
        mFuture = future;
    }

    /**
     * 任务对应的tag
     *
     * @return
     */
    public String getTag()
    {
        return mTag;
    }

    /**
     * 取消任务
     *
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return
     */
    public boolean cancel(boolean mayInterruptIfRunning)
    {
        return mFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * 任务是否被取消
     *
     * @return
     */
    public boolean isCancelled()
    {
        return mFuture.isCancelled();
    }

    /**
     * 任务是否已经完成
     *
     * @return
     */
    public boolean isDone()
    {
        return mFuture.isDone();
    }
}

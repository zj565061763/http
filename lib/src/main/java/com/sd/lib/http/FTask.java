package com.sd.lib.http;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;

abstract class FTask implements Runnable
{
    private final String mTag;
    private State mState = State.None;

    private OnStateChangeCallback mOnStateChangeCallback;

    public FTask()
    {
        this(null);
    }

    public FTask(String tag)
    {
        mTag = tag;
    }

    /**
     * 返回任务对应的tag
     *
     * @return
     */
    public final String getTag()
    {
        return mTag;
    }

    /**
     * 返回当前状态
     *
     * @return
     */
    public final State getState()
    {
        return mState;
    }

    /**
     * 设置状态变化回调
     *
     * @param callback
     */
    public void setOnStateChangeCallback(OnStateChangeCallback callback)
    {
        mOnStateChangeCallback = callback;
    }

    /**
     * 提交任务
     *
     * @return
     */
    public final FTaskInfo submit()
    {
        return FTaskManager.getInstance().submit(this, getTag(), mTaskCallback);
    }

    /**
     * 提交任务，按提交的顺序一个个执行
     *
     * @return
     */
    public final FTaskInfo submitSequence()
    {
        return FTaskManager.getInstance().submitSequence(this, getTag(), mTaskCallback);
    }

    /**
     * 提交要执行的任务
     *
     * @param executorService 要执行任务的线程池
     * @return
     */
    public final FTaskInfo submitTo(ExecutorService executorService)
    {
        return FTaskManager.getInstance().submitTo(this, getTag(), executorService, mTaskCallback);
    }

    /**
     * 取消任务
     *
     * @param mayInterruptIfRunning true-如果线程已经执行有可能被打断
     * @return
     */
    public final boolean cancel(boolean mayInterruptIfRunning)
    {
        return FTaskManager.getInstance().cancel(this, mayInterruptIfRunning);
    }

    private final FTaskManager.TaskCallback mTaskCallback = new FTaskManager.TaskCallback()
    {
        @Override
        public void onSubmit()
        {
            setState(State.Submit);
            FTask.this.onSubmit();
        }

        @Override
        public void onError(Throwable e)
        {
            setState(State.DoneError);
            FTask.this.onError(e);
        }

        @Override
        public void onCancel()
        {
            setState(State.DoneCancel);
            FTask.this.onCancel();
        }

        @Override
        public void onFinish()
        {
            if (getState() == State.Running)
                setState(State.DoneSuccess);

            FTask.this.onFinish();
        }
    };

    @Override
    public final void run()
    {
        setState(State.Running);

        try
        {
            onRun();
        } catch (Throwable e)
        {
            mTaskCallback.onError(e);
        }
    }

    /**
     * 执行回调（执行线程）
     *
     * @throws Throwable
     */
    protected abstract void onRun() throws Throwable;

    /**
     * 提交回调（提交线程）
     */
    protected void onSubmit()
    {
    }

    /**
     * 错误回调（执行线程）
     *
     * @param e
     */
    protected void onError(Throwable e)
    {
    }

    /**
     * 取消回调（执行线程）
     */
    protected void onCancel()
    {
    }

    /**
     * 结束回调（执行线程）
     */
    protected void onFinish()
    {
    }

    private void setState(State state)
    {
        if (state == null)
            throw new IllegalArgumentException("state is null");

        final State old = mState;
        if (old != state)
        {
            mState = state;

            if (mOnStateChangeCallback != null)
                mOnStateChangeCallback.onStateChanged(old, mState);
        }
    }

    public enum State
    {
        None,
        Submit,
        Running,
        DoneCancel,
        DoneError,
        DoneSuccess;

        public boolean isDone()
        {
            return this == DoneSuccess || this == DoneError || this == DoneCancel;
        }
    }

    /**
     * 状态变化回调
     */
    public interface OnStateChangeCallback
    {
        /**
         * 状态变化回调，不一定在主线程回调
         *
         * @param oldState
         * @param newState
         */
        void onStateChanged(State oldState, State newState);
    }

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable)
    {
        if (runnable == null)
            return;

        if (Looper.myLooper() == Looper.getMainLooper())
            runnable.run();
        else
            MAIN_HANDLER.post(runnable);
    }

    public static void removeCallbacks(Runnable runnable)
    {
        if (runnable == null)
            return;

        MAIN_HANDLER.removeCallbacks(runnable);
    }
}

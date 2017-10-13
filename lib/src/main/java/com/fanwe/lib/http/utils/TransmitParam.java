package com.fanwe.lib.http.utils;

/**
 * Created by zhengjun on 2017/10/13.
 */

public class TransmitParam
{
    private static final long CALCULATE_SPEED_SPAN = 100;

    private long mCurrent;
    private long mTotal;
    private int mProgress;
    private int mSpeedBps;

    private long mLastTime;
    private long mLastCount;

    public void transmit(long current, long total)
    {
        mCurrent = current;
        mTotal = total;

        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastTime > CALCULATE_SPEED_SPAN)
        {
            long count = current - mLastCount;
            mSpeedBps = (int) (count * (1000 / CALCULATE_SPEED_SPAN));

            mLastTime = currentTime;
            mLastCount = current;
        }

        mProgress = (int) (current * 100 / total);
    }

    public long getCurrent()
    {
        return mCurrent;
    }

    public long getTotal()
    {
        return mTotal;
    }

    public int getProgress()
    {
        return mProgress;
    }

    public int getSpeedBps()
    {
        return mSpeedBps;
    }

    public int getSpeedKBps()
    {
        return getSpeedBps() / 1024;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getCurrent()).append("/").append(getTotal()).append("\r\n")
                .append(getProgress()).append("%").append("\r\n")
                .append(getSpeedKBps()).append("KBps");
        return sb.toString();
    }
}

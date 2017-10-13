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
    private int mSpeed;

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
            mSpeed = (int) (count * (1000 / CALCULATE_SPEED_SPAN));

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

    public int getSpeed()
    {
        return mSpeed;
    }

    public int getSpeedKbps()
    {
        return getSpeed() / 1024;
    }

    public int getSpeedKBps()
    {
        return getSpeed() / (1024 * 8);
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

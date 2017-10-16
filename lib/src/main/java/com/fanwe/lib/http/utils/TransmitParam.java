package com.fanwe.lib.http.utils;

/**
 * Created by zhengjun on 2017/10/13.
 */

public class TransmitParam
{
    private static final long CALCULATE_SPEED_INTERVAL = 50;

    private long mCurrent;
    private long mTotal;
    private int mProgress;
    private int mSpeedBps;

    private long mLastTime;
    private long mLastCount;

    private long mCalculateSpeedInterval = CALCULATE_SPEED_INTERVAL;

    public synchronized void transmit(long current, long total)
    {
        mCurrent = current;
        mTotal = total;

        final long currentTime = System.currentTimeMillis();
        final long timeInterval = currentTime - mLastTime;
        if (timeInterval >= mCalculateSpeedInterval)
        {
            long count = current - mLastCount;
            mSpeedBps = (int) (count * (1000f / timeInterval));

            mLastTime = currentTime;
            mLastCount = current;
        }

        mProgress = (int) (current * 100 / total);
    }

    public synchronized boolean isFinish()
    {
        return mCurrent == mTotal && mCurrent > 0;
    }

    public synchronized void setCalculateSpeedInterval(long calculateSpeedInterval)
    {
        if (calculateSpeedInterval <= 0)
        {
            calculateSpeedInterval = CALCULATE_SPEED_INTERVAL;
        }
        mCalculateSpeedInterval = calculateSpeedInterval;
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

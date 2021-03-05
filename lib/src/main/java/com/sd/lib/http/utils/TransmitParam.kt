package com.sd.lib.http.utils

class TransmitParam {
    private val mCalculateSpeedInterval: Long
    private var mLastTime: Long = 0
    private var mLastCount: Long = 0

    @JvmOverloads
    constructor(calculateSpeedInterval: Long = 0) {
        val interval = if (calculateSpeedInterval > 0) calculateSpeedInterval else 100
        mCalculateSpeedInterval = interval
    }

    /** 当前传输量 */
    var current: Long = 0
        private set

    /** 总量 */
    var total: Long = 0
        private set

    /** 传输进度 */
    var progress = 0
        private set

    /** 传输速率(Bps) */
    var speedBps = 0
        private set

    /** 传输速率(KBps) */
    val speedKBps: Int
        get() = speedBps / 1024

    /** 传输是否完成 */
    val isComplete: Boolean
        get() = current > 0 && current == total

    /**
     * 拷贝对象
     */
    fun copy(): TransmitParam {
        val copy = TransmitParam(mCalculateSpeedInterval)
        copy.current = current
        copy.total = total
        copy.progress = progress
        copy.speedBps = speedBps
        copy.mLastTime = mLastTime
        copy.mLastCount = mLastCount
        return copy
    }

    /**
     * 传输
     *
     * @param total 总量
     * @param current 当前传输量
     * @return true-进度发生了变化
     */
    @Synchronized
    fun transmit(total: Long, current: Long): Boolean {
        val oldProgress = progress
        if (total <= 0 || current <= 0) {
            reset()
            return oldProgress != progress
        }

        this.total = total
        this.current = current

        val currentTime = System.currentTimeMillis()
        val interval = currentTime - mLastTime
        if (interval >= mCalculateSpeedInterval) {
            val count = current - mLastCount
            speedBps = (count * (1000f / interval)).toInt()
            mLastTime = currentTime
            mLastCount = current
        }

        progress = (current * 100 / total).toInt()
        return progress > oldProgress
    }

    private fun reset() {
        current = 0
        total = 0
        progress = 0
        speedBps = 0
        mLastTime = 0
        mLastCount = 0
    }

    override fun toString(): String {
        return "${current}/${total} ${progress}% ${speedKBps}KBps ${super.toString()}"
    }
}
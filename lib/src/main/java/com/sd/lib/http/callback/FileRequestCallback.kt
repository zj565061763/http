package com.sd.lib.http.callback

import com.sd.lib.http.utils.HttpIOUtils.Companion.closeQuietly
import com.sd.lib.http.utils.HttpIOUtils.Companion.copy
import com.sd.lib.http.utils.HttpUtils.Companion.removeCallbacks
import com.sd.lib.http.utils.HttpUtils.Companion.runOnUiThread
import com.sd.lib.http.utils.TransmitParam
import java.io.File
import java.io.FileOutputStream

abstract class FileRequestCallback : RequestCallback {
    val file: File
    val transmitParam = TransmitParam()

    constructor(file: File) {
        this.file = file
    }

    @Throws(Exception::class)
    override fun onSuccessBackground() {
        super.onSuccessBackground()
        val total = response!!.contentLength.toLong()
        val input = response!!.inputStream
        val output = FileOutputStream(file)
        try {
            copy(input, output) { count ->
                if (!requestTaskApi!!.isActive) {
                    // 任务已经死亡，停止下载
                    true
                }
                if (transmitParam.transmit(total, count)) {
                    runOnUiThread(mUpdateProgressRunnable)
                }
                false
            }
        } finally {
            runOnUiThread(mUpdateProgressRunnable)
            closeQuietly(input)
            closeQuietly(output)
        }
    }

    private val mUpdateProgressRunnable = Runnable { onProgressDownload(transmitParam) }

    protected abstract fun onProgressDownload(param: TransmitParam)

    override fun onCancel() {
        super.onCancel()
        removeCallbacks(mUpdateProgressRunnable)
    }
}
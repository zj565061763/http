package com.sd.lib.http.callback

import com.sd.lib.http.utils.HttpIOUtils
import com.sd.lib.http.utils.HttpUtils
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
        val total = response.contentLength.toLong()
        val input = response.inputStream
        val output = FileOutputStream(file)
        try {
            HttpIOUtils.copy(input, output) { count ->
                if (requestHandler.isActive) {
                    if (transmitParam.transmit(total, count)) {
                        HttpUtils.runOnUiThread(mUpdateProgressRunnable)
                    }
                    false
                } else {
                    true
                }
            }
        } finally {
            HttpUtils.runOnUiThread(mUpdateProgressRunnable)
            HttpIOUtils.closeQuietly(input)
            HttpIOUtils.closeQuietly(output)
        }
    }

    private val mUpdateProgressRunnable = Runnable { onProgressDownload(transmitParam) }

    protected abstract fun onProgressDownload(param: TransmitParam)

    override fun onCancel() {
        super.onCancel()
        HttpUtils.removeCallbacks(mUpdateProgressRunnable)
    }
}
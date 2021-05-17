package com.sd.lib.http.callback

import com.sd.lib.http.utils.HttpIOUtils
import com.sd.lib.http.utils.HttpUtils
import com.sd.lib.http.utils.TransmitParam
import java.io.File
import java.io.FileOutputStream

abstract class FileRequestCallback : RequestCallback {
    val file: File
    private val _transmitParam = TransmitParam()

    constructor(file: File) {
        this.file = file
    }

    @Throws(Exception::class)
    override fun onSuccessBackground() {
        super.onSuccessBackground()
        val total = httpResponse.contentLength.toLong()
        val input = httpResponse.inputStream
        val output = FileOutputStream(file)

        try {
            HttpIOUtils.copy(input, output) { count ->
                if (httpRequestHandler.isActive) {
                    if (_transmitParam.transmit(total, count)) {
                        val copyParam = _transmitParam.copy()
                        HttpUtils.runOnUiThread { onProgressDownload(copyParam) }
                    }
                    false
                } else {
                    true
                }
            }
        } finally {
            HttpIOUtils.closeQuietly(input)
            HttpIOUtils.closeQuietly(output)
        }
    }

    protected abstract fun onProgressDownload(param: TransmitParam)
}
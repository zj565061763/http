package com.sd.lib.http.callback

import com.sd.lib.http.utils.TransmitParam

interface IUploadCallback {
    fun onProgressUpload(params: TransmitParam)
}
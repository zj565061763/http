package com.sd.lib.http.callback

import com.sd.lib.http.utils.TransmitParam

interface IUploadProgressCallback {
    fun onProgressUpload(param: TransmitParam)
}
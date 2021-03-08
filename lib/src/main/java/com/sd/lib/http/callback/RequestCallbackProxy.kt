package com.sd.lib.http.callback

import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.utils.TransmitParam

class RequestCallbackProxy : RequestCallback {
    private val callbacks: Array<out RequestCallback>

    private constructor(vararg callbacks: RequestCallback) {
        this.callbacks = callbacks
    }

    override fun saveRequest(request: IRequest) {
        for (item in callbacks) {
            item?.saveRequest(request)
        }
    }

    override fun saveResponse(response: IResponse) {
        for (item in callbacks) {
            item?.saveResponse(response)
        }
    }

    override fun onPrepare(request: IRequest) {
        for (item in callbacks) {
            item?.onPrepare(request)
        }
    }

    override fun onStart() {
        for (item in callbacks) {
            item?.onStart()
        }
    }

    @Throws(Exception::class)
    override fun onSuccessBackground() {
        for (item in callbacks) {
            item?.onSuccessBackground()
        }
    }

    override fun onSuccessBefore() {
        for (item in callbacks) {
            item?.onSuccessBefore()
        }
    }

    override fun onSuccess() {
        for (item in callbacks) {
            item?.onSuccess()
        }
    }

    override fun onError(e: Exception) {
        for (item in callbacks) {
            item?.onError(e)
        }
    }

    override fun onCancel() {
        for (item in callbacks) {
            item?.onCancel()
        }
    }

    override fun onFinish() {
        for (item in callbacks) {
            item?.onFinish()
        }
    }

    override fun onProgressUpload(param: TransmitParam) {
        for (item in callbacks) {
            item?.onProgressUpload(param)
        }
    }

    companion object {
        @JvmStatic
        fun get(vararg callbacks: RequestCallback): RequestCallback {
            return RequestCallbackProxy(*callbacks)
        }
    }
}
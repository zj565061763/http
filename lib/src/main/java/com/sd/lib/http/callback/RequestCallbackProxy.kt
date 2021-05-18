package com.sd.lib.http.callback

import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.RequestHandler
import com.sd.lib.http.utils.TransmitParam

class RequestCallbackProxy private constructor(callbacks: Array<out RequestCallback>) : RequestCallback() {
    private val callbacks: Array<out RequestCallback> = callbacks

    override fun saveRequest(request: IRequest) {
        for (item in callbacks) {
            item?.saveRequest(request)
        }
    }

    override fun saveRequestHandler(requestHandler: RequestHandler) {
        for (item in callbacks) {
            item?.saveRequestHandler(requestHandler)
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
            return RequestCallbackProxy(callbacks)
        }
    }
}
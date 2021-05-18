package com.sd.lib.http.callback

import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.RequestHandler
import com.sd.lib.http.utils.TransmitParam

class RequestCallbackProxy private constructor(callbacks: Array<out RequestCallback>) : RequestCallback() {
    private val callbacks: Array<out RequestCallback> = callbacks

    override fun saveRequest(request: IRequest) {
        for (item in callbacks) {
            item.saveRequest(request)
        }
    }

    override fun saveRequestHandler(requestHandler: RequestHandler) {
        for (item in callbacks) {
            item.saveRequestHandler(requestHandler)
        }
    }

    override fun notifyPrepare() {
        for (item in callbacks) {
            item.notifyPrepare()
        }
    }

    override fun notifyStart() {
        for (item in callbacks) {
            item.notifyStart()
        }
    }

    override fun notifyResponse(response: IResponse) {
        for (item in callbacks) {
            item.notifyResponse(response)
        }
    }

    override fun notifySuccess() {
        for (item in callbacks) {
            item.notifySuccess()
        }
    }

    override fun onSuccess() {
    }

    override fun notifyError(e: Exception) {
        for (item in callbacks) {
            item.notifyError(e)
        }
    }

    override fun notifyCancel() {
        for (item in callbacks) {
            item.notifyCancel()
        }
    }

    override fun notifyFinish() {
        for (item in callbacks) {
            item.notifyFinish()
        }
    }

    override fun onProgressUpload(param: TransmitParam) {
        for (item in callbacks) {
            item.onProgressUpload(param)
        }
    }

    companion object {
        @JvmStatic
        fun get(vararg callbacks: RequestCallback): RequestCallback {
            return RequestCallbackProxy(callbacks)
        }
    }
}
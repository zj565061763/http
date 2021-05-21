package com.sd.demo.http.utils

import androidx.annotation.CallSuper
import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.callback.RequestCallback
import com.sd.lib.http.callback.RequestCallbackProxy

open class LifecycleRequestCallback : RequestCallback() {
    val list = mutableListOf<Lifecycle>()

    fun checkLifecycle(vararg lifecycle: Lifecycle) {
        val listParams = lifecycle.toList()
        assert(list !== listParams)
        assert(list == listParams)
    }

    fun checkLifecycle(callback: LifecycleRequestCallback) {
        assert(list !== callback.list)
        assert(list == callback.list)
    }

    @CallSuper
    override fun onPrepare(request: IRequest) {
        super.onPrepare(request)
        list.add(Lifecycle.onPrepare)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        list.add(Lifecycle.onStart)
    }

    @CallSuper
    override fun onResponseBackground(response: IResponse) {
        super.onResponseBackground(response)
        list.add(Lifecycle.onResponseBackground)
    }

    @CallSuper
    override fun onSuccessBefore() {
        super.onSuccessBefore()
        list.add(Lifecycle.onSuccessBefore)
    }

    @CallSuper
    override fun onSuccess() {
        list.add(Lifecycle.onSuccess)
    }

    @CallSuper
    override fun onError(e: Exception) {
        super.onError(e)
        list.add(Lifecycle.onError)
    }

    @CallSuper
    override fun onCancel() {
        super.onCancel()
        list.add(Lifecycle.onCancel)
    }

    @CallSuper
    override fun onFinish() {
        super.onFinish()
        list.add(Lifecycle.onFinish)
    }
}

fun getProxyCallback(callback: LifecycleRequestCallback): RequestCallback {
    val lastCallback = object : LifecycleRequestCallback() {
        override fun onFinish() {
            super.onFinish()
            callback.checkLifecycle(this)
        }
    }
    return RequestCallbackProxy.get(callback, lastCallback)
}

val callbackNormal
    get() = object : LifecycleRequestCallback() {
        override fun onFinish() {
            super.onFinish()
            checkLifecycle(
                Lifecycle.onPrepare,
                Lifecycle.onStart,
                Lifecycle.onResponseBackground,
                Lifecycle.onSuccessBefore,
                Lifecycle.onSuccess,
                Lifecycle.onFinish,
            )
        }
    }

val callbackCancel_onStart
    get() = object : LifecycleRequestCallback() {
        override fun onStart() {
            super.onStart()
            httpRequestHandler.cancel()
        }

        override fun onFinish() {
            super.onFinish()
            checkLifecycle(
                Lifecycle.onPrepare,
                Lifecycle.onStart,
                Lifecycle.onCancel,
                Lifecycle.onFinish,
            )
        }
    }

val callbackCancel_onResponseBackground
    get() = object : LifecycleRequestCallback() {
        override fun onResponseBackground(response: IResponse) {
            super.onResponseBackground(response)
            httpRequestHandler.cancel()
        }

        override fun onFinish() {
            super.onFinish()
            checkLifecycle(
                Lifecycle.onPrepare,
                Lifecycle.onStart,
                Lifecycle.onResponseBackground,
                Lifecycle.onCancel,
                Lifecycle.onFinish,
            )
        }
    }

val callbackCancel_onSuccess
    get() = object : LifecycleRequestCallback() {
        override fun onSuccessBefore() {
            super.onSuccessBefore()
            httpRequestHandler.cancel()
        }

        override fun onSuccess() {
            super.onSuccess()
            httpRequestHandler.cancel()
        }

        override fun onFinish() {
            super.onFinish()
            checkLifecycle(
                Lifecycle.onPrepare,
                Lifecycle.onStart,
                Lifecycle.onResponseBackground,
                Lifecycle.onSuccessBefore,
                Lifecycle.onSuccess,
                Lifecycle.onFinish,
            )
        }
    }

val callbackCancel_onError
    get() = object : LifecycleRequestCallback() {
        override fun onError(e: Exception) {
            super.onError(e)
            httpRequestHandler.cancel()
        }

        override fun onFinish() {
            super.onFinish()
            checkLifecycle(
                Lifecycle.onPrepare,
                Lifecycle.onStart,
                Lifecycle.onResponseBackground,
                Lifecycle.onSuccessBefore,
                Lifecycle.onSuccess,
                Lifecycle.onFinish,
            )
        }
    }

val callbackException_onResponseBackground
    get() = object : LifecycleRequestCallback() {
        override fun onResponseBackground(response: IResponse) {
            super.onResponseBackground(response)
            throw RuntimeException("RuntimeException")
        }

        override fun onFinish() {
            super.onFinish()
            checkLifecycle(
                Lifecycle.onPrepare,
                Lifecycle.onStart,
                Lifecycle.onResponseBackground,
                Lifecycle.onError,
                Lifecycle.onFinish,
            )
        }
    }

enum class Lifecycle {
    onPrepare,
    onStart,
    onResponseBackground,
    onSuccessBefore,
    onSuccess,
    onError,
    onCancel,
    onFinish,
}
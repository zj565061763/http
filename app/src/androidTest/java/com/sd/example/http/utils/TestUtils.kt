package com.sd.example.http.utils

import androidx.annotation.CallSuper
import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.callback.RequestCallback

open class LifecycleRequestCallback : RequestCallback() {
    val list = mutableListOf<Lifecycle>()

    fun checkLifecycle(vararg lifecycle: Lifecycle) {
        val listParams = lifecycle.toList()
        assert(list !== listParams)
        assert(list == listParams)
    }

    @CallSuper
    override fun onPrepare(request: IRequest) {
        super.onPrepare(request)
        assert(!list.contains(Lifecycle.onPrepare))
        list.add(Lifecycle.onPrepare)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        assert(!list.contains(Lifecycle.onStart))
        list.add(Lifecycle.onStart)
    }

    @CallSuper
    override fun onResponseBackground(response: IResponse) {
        super.onResponseBackground(response)
        assert(!list.contains(Lifecycle.onResponseBackground))
        list.add(Lifecycle.onResponseBackground)
    }

    @CallSuper
    override fun onSuccessBefore() {
        super.onSuccessBefore()
        assert(!list.contains(Lifecycle.onSuccessBefore))
        list.add(Lifecycle.onSuccessBefore)
    }

    @CallSuper
    override fun onSuccess() {
        assert(!list.contains(Lifecycle.onSuccess))
        list.add(Lifecycle.onSuccess)
    }

    @CallSuper
    override fun onError(e: Exception) {
        super.onError(e)
        assert(!list.contains(Lifecycle.onError))
        list.add(Lifecycle.onError)
    }

    @CallSuper
    override fun onCancel() {
        super.onCancel()
        assert(!list.contains(Lifecycle.onCancel))
        list.add(Lifecycle.onCancel)
    }

    @CallSuper
    override fun onFinish() {
        super.onFinish()
        assert(!list.contains(Lifecycle.onFinish))
        list.add(Lifecycle.onFinish)
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
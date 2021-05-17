package com.sd.lib.http.utils

import android.os.Handler
import android.os.Looper

internal object HttpUtils {
    private val _mainHandler by lazy { Handler(Looper.getMainLooper()) }

    fun runOnUiThread(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            _mainHandler.post(runnable)
        }
    }

    fun checkBackgroundThread() {
        assert(Looper.myLooper() != Looper.getMainLooper())
    }

    fun checkMainThread() {
        assert(Looper.myLooper() == Looper.getMainLooper())
    }
}
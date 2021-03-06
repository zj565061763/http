package com.sd.lib.http.utils

import android.os.Handler
import android.os.Looper

internal class HttpUtils private constructor() {

    companion object {
        private val mainHandler by lazy {
            Handler(Looper.getMainLooper())
        }

        @JvmStatic
        fun runOnUiThread(runnable: Runnable?) {
            if (runnable == null) return
            if (Looper.myLooper() == Looper.getMainLooper()) runnable.run() else mainHandler.post(runnable)
        }
    }
}
package com.sd.lib.http.utils

import android.util.Log
import com.sd.lib.http.RequestManager

internal object HttpLog {
    private const val TAG = "FHttp"

    @JvmStatic
    fun i(msg: String) {
        if (RequestManager.isDebug) {
            Log.i(TAG, msg)
        }
    }

    @JvmStatic
    fun e(msg: String) {
        if (RequestManager.isDebug) {
            Log.e(TAG, msg)
        }
    }
}
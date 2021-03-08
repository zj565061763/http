package com.sd.lib.http.utils

import android.util.Log
import com.sd.lib.http.RequestManager

internal class HttpLog private constructor() {

    companion object {
        private const val TAG = "FHttp"

        @JvmStatic
        fun i(msg: String?) {
            if (RequestManager.instance.isDebug) {
                Log.i(TAG, msg)
            }
        }

        @JvmStatic
        fun e(msg: String?) {
            if (RequestManager.instance.isDebug) {
                Log.e(TAG, msg)
            }
        }
    }
}
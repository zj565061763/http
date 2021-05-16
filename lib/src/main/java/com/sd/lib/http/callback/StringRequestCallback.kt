package com.sd.lib.http.callback

import androidx.annotation.CallSuper

abstract class StringRequestCallback : RequestCallback() {
    /** 返回请求的字符串内容，onSuccessXXXX方法中才可以访问 */
    lateinit var result: String
        private set

    @CallSuper
    @Throws(Exception::class)
    override fun onSuccessBackground() {
        super.onSuccessBackground()
        result = httpResponse.readString()
    }
}
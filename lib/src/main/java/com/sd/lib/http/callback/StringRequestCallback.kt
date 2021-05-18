package com.sd.lib.http.callback

import com.sd.lib.http.IResponse

abstract class StringRequestCallback : RequestCallback() {
    /** 返回请求的字符串内容，onSuccessXXXX方法中不为null */
    var result: String? = null
        private set

    @Throws(Exception::class)
    override fun onSuccessBackground(response: IResponse) {
        super.onSuccessBackground(response)
        result = response.readString()
    }
}
package com.sd.lib.http.body

import android.text.TextUtils
import com.sd.lib.http.ContentType

open class StringBody : IRequestBody<String> {

    constructor(body: String?, contentType: String?) {
        this.body = if (TextUtils.isEmpty(body)) "" else body!!
        this.contentType = if (TextUtils.isEmpty(contentType)) ContentType.STREAM else contentType!!
    }

    override val body: String

    override val contentType: String
}
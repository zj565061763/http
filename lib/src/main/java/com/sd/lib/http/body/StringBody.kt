package com.sd.lib.http.body

import android.text.TextUtils
import com.sd.lib.http.ContentType

open class StringBody(content: String?, contentType: String?) : IRequestBody<String> {

    override val body: String = content ?: ""

    override val contentType: String = if (TextUtils.isEmpty(contentType)) ContentType.STREAM else contentType!!
}
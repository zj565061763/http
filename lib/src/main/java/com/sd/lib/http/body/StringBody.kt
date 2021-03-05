package com.sd.lib.http.body

import android.text.TextUtils
import com.sd.lib.http.ContentType
import java.nio.charset.Charset

open class StringBody(body: String?, charset: Charset? = null, contentType: String? = null) : CharsetBody<String?>(charset) {
    override val contentType: String = if (TextUtils.isEmpty(contentType)) ContentType.STREAM else contentType!!
    override val body: String = if (TextUtils.isEmpty(body)) "" else body!!
}
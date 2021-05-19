package com.sd.lib.http.body

import com.sd.lib.http.ContentType

open class StringBody(content: String?, contentType: String?) : IRequestBody<String> {

    override val body: String = content ?: ""

    override val contentType: String = if (contentType == null || contentType.isEmpty()) ContentType.STREAM else contentType
}
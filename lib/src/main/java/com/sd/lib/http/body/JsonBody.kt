package com.sd.lib.http.body

import com.sd.lib.http.ContentType
import java.nio.charset.Charset

class JsonBody @JvmOverloads constructor(body: String?, charset: Charset? = null) : StringBody(body, charset, ContentType.JSON)
package com.sd.lib.http.body

import java.nio.charset.Charset

abstract class CharsetBody<T>(charset: Charset?) : IRequestBody<T> {
    val charset: Charset = charset ?: Charset.defaultCharset()
}
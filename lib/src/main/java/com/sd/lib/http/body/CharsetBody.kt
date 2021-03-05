package com.sd.lib.http.body

import java.nio.charset.Charset

abstract class CharsetBody<T> : IRequestBody<T> {
    constructor(charset: Charset?) {
        this.charset = charset ?: Charset.defaultCharset()
    }

    val charset: Charset
}
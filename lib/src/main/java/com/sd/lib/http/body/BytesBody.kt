package com.sd.lib.http.body

import com.sd.lib.http.ContentType

class BytesBody : IRequestBody<ByteArray> {

    constructor(body: ByteArray) {
        this.body = body
    }

    override val contentType: String = ContentType.STREAM

    override val body: ByteArray
}
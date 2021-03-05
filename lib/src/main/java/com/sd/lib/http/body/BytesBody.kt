package com.sd.lib.http.body

import com.sd.lib.http.ContentType

class BytesBody : IRequestBody<ByteArray> {

    constructor(bytes: ByteArray) {
        this.body = bytes
    }

    override val body: ByteArray

    override val contentType: String = ContentType.STREAM
}
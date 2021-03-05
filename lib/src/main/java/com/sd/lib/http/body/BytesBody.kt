package com.sd.lib.http.body

import com.sd.lib.http.ContentType

class BytesBody(bytes: ByteArray) : IRequestBody<ByteArray> {

    override val contentType: String = ContentType.STREAM

    override val body: ByteArray = bytes
}
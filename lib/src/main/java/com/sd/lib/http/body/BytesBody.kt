package com.sd.lib.http.body

import com.sd.lib.http.ContentType

class BytesBody(bytes: ByteArray) : IRequestBody<ByteArray> {

    override val body: ByteArray = bytes

    override val contentType: String = ContentType.STREAM
}
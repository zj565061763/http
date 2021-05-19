package com.sd.lib.http.body

import com.sd.lib.http.ContentType
import java.io.File
import java.net.HttpURLConnection

class FileBody(file: File) : IRequestBody<File> {

    override val body: File = file

    override val contentType: String by lazy {
        var contentType = HttpURLConnection.guessContentTypeFromName(body.name)
        if (contentType == null || contentType.isEmpty()) {
            contentType = ContentType.STREAM
        }
        contentType
    }
}
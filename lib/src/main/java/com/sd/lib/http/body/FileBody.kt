package com.sd.lib.http.body

import android.text.TextUtils
import com.sd.lib.http.ContentType
import java.io.File
import java.net.HttpURLConnection

class FileBody : IRequestBody<File> {

    constructor(file: File) {
        this.body = file
    }

    override val contentType: String
        get() {
            var contentType = HttpURLConnection.guessContentTypeFromName(body.name)
            if (TextUtils.isEmpty(contentType)) {
                contentType = ContentType.STREAM
            }
            return contentType
        }

    override val body: File
}
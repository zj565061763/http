package com.sd.lib.http.body

import android.text.TextUtils
import com.sd.lib.http.ContentType
import java.io.File
import java.net.HttpURLConnection

class FileBody(file: File) : IRequestBody<File> {

    override val contentType: String
        get() {
            var contentType = HttpURLConnection.guessContentTypeFromName(body.name)
            if (TextUtils.isEmpty(contentType)) {
                contentType = ContentType.STREAM
            }
            return contentType
        }

    override val body: File = file
}
package com.sd.lib.http.body

import com.sd.lib.http.ContentType

class JsonBody(body: String?) : StringBody(body, ContentType.JSON) {
}
package com.sd.lib.http.body

import com.sd.lib.http.ContentType

class JsonBody : StringBody {
    constructor(body: String?) : super(body, ContentType.JSON)
}
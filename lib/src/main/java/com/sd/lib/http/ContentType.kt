package com.sd.lib.http

class ContentType private constructor() {
    companion object {
        const val STREAM = "application/octet-stream"
        const val JSON = "application/json"
        const val FORM = "application/x-www-form-urlencoded"
        const val FORM_MULTIPART = "multipart/form-data"
    }
}
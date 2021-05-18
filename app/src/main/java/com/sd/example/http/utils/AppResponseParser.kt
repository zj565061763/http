package com.sd.example.http.utils

import android.util.Log
import com.sd.lib.http.IRequest
import com.sd.lib.http.parser.DefaultResponseParser

class AppResponseParser : DefaultResponseParser() {
    val TAG = AppResponseParser::class.java.simpleName

    override fun <T> parse(content: String, clazz: Class<T>, request: IRequest): T {
        Log.i(TAG, "parse content:${content} clazz:${clazz.name} request:${request}")
        return super.parse(content, clazz, request)
    }
}
package com.sd.example.http.utils

import android.util.Log
import com.sd.lib.http.IRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.parser.DefaultResponseParser

class AppResponseParser : DefaultResponseParser() {
    val TAG = AppResponseParser::class.java.simpleName

    override fun <T> parse(response: IResponse, clazz: Class<T>, request: IRequest): T {
        val content = response.readString()
        Log.i(TAG, "parse content:${content} clazz:${clazz.name} request:${request}")
        return super.parse(response, clazz, request)
    }
}
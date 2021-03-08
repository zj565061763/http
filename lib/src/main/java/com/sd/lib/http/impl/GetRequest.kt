package com.sd.lib.http.impl

import com.sd.lib.http.IGetRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.exception.HttpException
import com.sd.lib.http.impl.HttpRequest

class GetRequest : BaseRequestImpl(), IGetRequest {

    @Throws(HttpException::class)
    override fun doExecute(): IResponse {
        val getUrl = HttpRequest.append(url, params.toMap())
        val request = newHttpRequest(getUrl, HttpRequest.METHOD_GET)
        val response = Response(request)
        response.code
        return response
    }
}
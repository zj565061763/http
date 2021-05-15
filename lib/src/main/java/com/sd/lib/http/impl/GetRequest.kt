package com.sd.lib.http.impl

import com.sd.lib.http.IGetRequest
import com.sd.lib.http.IResponse
import com.sd.lib.http.impl.HttpRequest

class GetRequest : BaseRequestImpl(), IGetRequest {
    override fun doExecute(): IResponse {
        val getUrl = HttpRequest.append(url, params.toMap())
        val request = newHttpRequest(getUrl, HttpRequest.METHOD_GET)
        return Response(request)
    }
}
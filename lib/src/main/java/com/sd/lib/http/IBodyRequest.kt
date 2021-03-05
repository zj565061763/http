package com.sd.lib.http

import com.sd.lib.http.body.IRequestBody

interface IBodyRequest {
    fun setBody(body: IRequestBody<*>)
}
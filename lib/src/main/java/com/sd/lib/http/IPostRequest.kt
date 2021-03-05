package com.sd.lib.http

import java.io.File

interface IPostRequest : IRequest, IBodyRequest {

    var paramsType: ParamsType

    fun addPart(name: String, file: File)

    fun addPart(name: String, file: File, filename: String? = null, contentType: String? = null)

    enum class ParamsType {
        Default, Json
    }
}
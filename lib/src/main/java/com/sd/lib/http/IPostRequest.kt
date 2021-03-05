package com.sd.lib.http

import java.io.File

interface IPostRequest : IRequest, IBodyRequest {

    fun addPart(name: String, file: File)

    fun addPart(name: String, file: File, filename: String? = null, contentType: String? = null)

    fun setParamsType(type: ParamsType)

    enum class ParamsType {
        Default, Json
    }
}
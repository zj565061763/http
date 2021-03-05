package com.sd.lib.http.body

interface IRequestBody<T> {

    val body: T

    val contentType: String
}
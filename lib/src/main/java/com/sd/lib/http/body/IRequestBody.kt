package com.sd.lib.http.body

interface IRequestBody<T> {
    val contentType: String
    val body: T
}
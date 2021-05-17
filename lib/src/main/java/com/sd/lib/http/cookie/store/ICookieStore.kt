package com.sd.lib.http.cookie.store

import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI

interface ICookieStore : CookieStore {
    fun add(uri: URI?, listCookie: List<HttpCookie?>?)
}
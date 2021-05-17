package com.sd.lib.http.cookie.store

import java.net.CookieStore
import java.net.HttpCookie
import java.net.URI

interface ICookieStore : CookieStore {
    fun add(uri: URI?, listCookie: List<HttpCookie>)

    override fun add(uri: URI?, cookie: HttpCookie)

    override fun get(uri: URI): List<HttpCookie>

    override fun getCookies(): List<HttpCookie>

    override fun getURIs(): List<URI?>

    override fun remove(uri: URI?, cookie: HttpCookie): Boolean

    override fun removeAll(): Boolean
}
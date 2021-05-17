package com.sd.lib.http.cookie.store

import com.sd.lib.http.cookie.ICookieStore
import com.sd.lib.http.cookie.SerializableHttpCookie
import java.net.HttpCookie
import java.net.URI

abstract class SimpleCookieStore : ICookieStore {
    private val _cookie by lazy { mutableMapOf<URI, List<SerializableHttpCookie>>() }
    private val _cookieStore by lazy { MemoryCookieStore() }

    override fun add(uri: URI?, listCookie: List<HttpCookie?>?) {
        _cookieStore.add(uri, listCookie)
    }

    override fun add(uri: URI?, cookie: HttpCookie?) {
        _cookieStore.add(uri, cookie)
    }

    override fun get(uri: URI?): MutableList<HttpCookie> {
        return _cookieStore.get(uri)
    }

    override fun getCookies(): MutableList<HttpCookie> {
        return _cookieStore.cookies
    }

    override fun getURIs(): MutableList<URI> {
        return _cookieStore.urIs
    }

    override fun remove(uri: URI?, cookie: HttpCookie?): Boolean {
        return _cookieStore.remove(uri, cookie)
    }

    override fun removeAll(): Boolean {
        return _cookieStore.removeAll()
    }
}
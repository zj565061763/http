package com.sd.lib.http.cookie.store

import java.net.HttpCookie
import java.net.URI

internal class MemoryCookieStore : ICookieStore {
    private val _cookieStore by lazy { InMemoryCookieStore() }

    override fun add(uri: URI?, listCookie: List<HttpCookie?>?) {
        listCookie?.forEach {
            _cookieStore.add(uri, it)
        }
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
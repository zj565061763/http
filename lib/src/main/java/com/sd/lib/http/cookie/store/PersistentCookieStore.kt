package com.sd.lib.http.cookie.store

import android.content.Context
import com.sd.lib.http.cookie.SerializableHttpCookie
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpCookie
import java.net.URI

abstract class PersistentCookieStore : ICookieStore {
    val context: Context
    private val _cookieStore = InMemoryCookieStore()

    constructor(context: Context) {
        this.context = context.applicationContext
        getCache()
    }

    override fun add(uri: URI?, listCookie: List<HttpCookie>) {
        listCookie.forEach {
            _cookieStore.add(uri, it)
        }
        saveCache()
    }

    override fun add(uri: URI?, cookie: HttpCookie) {
        _cookieStore.add(uri, cookie)
        saveCache()
    }

    override fun get(uri: URI): List<HttpCookie> {
        return _cookieStore.get(uri)
    }

    override fun getCookies(): List<HttpCookie> {
        return _cookieStore.cookies
    }

    override fun getURIs(): List<URI?> {
        return _cookieStore.urIs
    }

    override fun remove(uri: URI?, cookie: HttpCookie): Boolean {
        val remove = _cookieStore.remove(uri, cookie)
        if (remove) {
            saveCache()
        }
        return remove
    }

    override fun removeAll(): Boolean {
        val removeAll = _cookieStore.removeAll()
        if (removeAll) {
            saveCache()
        }
        return removeAll
    }

    private fun saveCache() {
        GlobalScope.launch {
            val mapCookie = _cookieStore.copyCookie()
            val mapCache = mutableMapOf<URI?, List<SerializableHttpCookie>>()

            for ((key, value) in mapCookie) {
                val listCopy = mutableListOf<SerializableHttpCookie>()
                mapCache.put(key, listCopy)

                value.forEach { item ->
                    val cookie = SerializableHttpCookie.from(item)
                    listCopy.add(cookie)
                }
            }
            saveCacheImpl(mapCache)
        }
    }

    private fun getCache() {
        val mapCache = getCacheImpl()
        if (mapCache == null || mapCache.isEmpty()) return

        for ((key, value) in mapCache) {
            value.forEach { item ->
                _cookieStore.add(key, item.toHttpCookie())
            }
        }
    }

    /**
     * 保存到缓存
     */
    protected abstract fun saveCacheImpl(cookies: Map<URI?, List<SerializableHttpCookie>>)

    /**
     * 读取缓存
     */
    protected abstract fun getCacheImpl(): Map<URI?, List<SerializableHttpCookie>>?
}
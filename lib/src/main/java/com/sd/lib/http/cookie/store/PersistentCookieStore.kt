package com.sd.lib.http.cookie.store

import android.content.Context
import com.sd.lib.http.cookie.SerializableHttpCookie
import java.net.HttpCookie
import java.net.URI

abstract class PersistentCookieStore : ICookieStore {
    val context: Context
    private val _cookieStore = InMemoryCookieStore()

    constructor(context: Context) {
        this.context = context.applicationContext
        getCache()
    }

    override fun add(uri: URI?, listCookie: List<HttpCookie?>?) {
        if (listCookie != null) {
            listCookie.forEach {
                _cookieStore.add(uri, it)
            }
            saveCache()
        }
    }

    override fun add(uri: URI?, cookie: HttpCookie?) {
        _cookieStore.add(uri, cookie)
        saveCache()
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
        _cookieStore.runLock {
            val map = mutableMapOf<URI, List<SerializableHttpCookie>>()
            for ((key, value) in _cookieStore.uriIndex) {
                val listCopy = mutableListOf<SerializableHttpCookie>()
                map.put(key, listCopy)

                value.forEach { item ->
                    val cookie = SerializableHttpCookie.from(item)
                    if (cookie != null) {
                        listCopy.add(cookie)
                    }
                }
            }
            saveCacheImpl(map)
        }
    }

    private fun getCache() {
        _cookieStore.runLock {
            val map = getCacheImpl()
            if (map == null || map.isEmpty()) return@runLock

            for ((key, value) in map) {
                val listCopy = mutableListOf<HttpCookie>()
                _cookieStore.uriIndex.put(key, listCopy)

                value.forEach { item ->
                    val cookie = item?.toHttpCookie()
                    if (cookie != null) {
                        listCopy.add(cookie)
                    }
                }
            }
        }
    }

    /**
     * 保存到缓存
     */
    protected abstract fun saveCacheImpl(cookies: Map<URI, List<SerializableHttpCookie>>)

    /**
     * 读取缓存
     */
    protected abstract fun getCacheImpl(): Map<URI, List<SerializableHttpCookie>>?
}
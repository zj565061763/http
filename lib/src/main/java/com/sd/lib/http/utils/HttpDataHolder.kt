package com.sd.lib.http.utils

import java.util.*

class HttpDataHolder<K, V> {
    private val _map = mutableMapOf<K, V>()

    fun put(key: K, value: V?): HttpDataHolder<K, V> {
        if (value == null) {
            _map.remove(key)
        } else {
            _map[key] = value
        }
        return this
    }

    fun put(map: Map<K, V>?): HttpDataHolder<K, V> {
        if (map != null) {
            for ((key, value) in map) {
                put(key, value)
            }
        }
        return this
    }

    fun put(data: HttpDataHolder<K, V>?): HttpDataHolder<K, V> {
        data?.let {
            put(it.toMap())
        }
        return this
    }

    operator fun get(key: Any): V? {
        return _map[key]
    }

    fun size(): Int {
        return _map.size
    }

    fun clear(): HttpDataHolder<K, V> {
        _map.clear()
        return this
    }

    fun toMap(): Map<K, V> {
        return LinkedHashMap(_map)
    }
}
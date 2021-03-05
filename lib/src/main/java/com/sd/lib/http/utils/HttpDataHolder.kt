package com.sd.lib.http.utils

import java.util.*

class HttpDataHolder<K, V> {
    private val mMap: MutableMap<K, V> = LinkedHashMap()

    fun put(key: K, value: V?): HttpDataHolder<K, V> {
        if (value == null) {
            mMap.remove(key)
        } else {
            mMap[key] = value
        }
        return this
    }

    fun put(map: Map<K, V>?): HttpDataHolder<K, V> {
        map?.let {
            for ((key, value) in it) {
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
        return mMap[key]
    }

    fun size(): Int {
        return mMap.size
    }

    fun clear(): HttpDataHolder<K, V> {
        mMap.clear()
        return this
    }

    fun toMap(): Map<K, V> {
        return LinkedHashMap(mMap)
    }
}
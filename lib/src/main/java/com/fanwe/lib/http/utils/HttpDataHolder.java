package com.fanwe.lib.http.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhengjun on 2018/3/14.
 */
public class HttpDataHolder<K, V>
{
    private final Map<K, V> mMap = new LinkedHashMap<>();

    public HttpDataHolder<K, V> put(K key, V value)
    {
        if (value == null)
        {
            mMap.remove(key);
        } else
        {
            mMap.put(key, value);
        }
        return this;
    }

    public HttpDataHolder<K, V> putAll(Map<K, V> map)
    {
        if (map != null)
        {
            for (Map.Entry<K, V> item : map.entrySet())
            {
                put(item.getKey(), item.getValue());
            }
        }
        return this;
    }

    public V get(Object key)
    {
        return mMap.get(key);
    }

    public int size()
    {
        return mMap.size();
    }

    public HttpDataHolder<K, V> clear()
    {
        mMap.clear();
        return this;
    }

    public Map<K, V> toMap()
    {
        return new LinkedHashMap<>(mMap);
    }
}

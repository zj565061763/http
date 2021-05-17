package com.sd.lib.http.cookie.store

import android.content.Context
import com.sd.lib.http.cookie.SerializableHttpCookie
import com.sd.lib.http.utils.HttpIOUtils
import com.sd.lib.http.utils.HttpLog
import java.io.*
import java.net.URI

class SerializableCookieStore : PersistentCookieStore {

    private var _file: File? = null
        get() {
            if (field == null) {
                field = File(context.filesDir, "f_http_cookie")
            }
            return field
        }

    constructor(context: Context) : super(context)

    @Synchronized
    override fun saveCacheImpl(cookies: Map<URI?, List<SerializableHttpCookie>>) {
        try {
            val file = _file!!
            if (!file.exists()) {
                file.createNewFile()
            }

            val holder = SerializableCookieHolder().apply {
                this.map = cookies
            }

            serializeObject(holder, file)
            HttpLog.i("cookie CookieStore saveCacheImpl success")
        } catch (e: Exception) {
            HttpLog.e("cookie CookieStore saveCacheImpl error:${e}")
        }
    }

    override fun getCacheImpl(): Map<URI?, List<SerializableHttpCookie>>? {
        try {
            val file = _file!!
            val obj = deserializeObject<SerializableCookieHolder>(file)
            HttpLog.i("cookie CookieStore getCacheImpl obj:${obj}")
            return obj?.map
        } catch (e: Exception) {
            HttpLog.e("cookie CookieStore getCacheImpl error:${e}")
            return null
        }
    }

    companion object {
        private fun <T : Serializable> serializeObject(obj: T, file: File) {
            var oos: ObjectOutputStream? = null
            try {
                oos = ObjectOutputStream(FileOutputStream(file))
                oos.writeObject(obj)
                oos.flush()
            } finally {
                HttpIOUtils.closeQuietly(oos)
            }
        }

        private fun <T : Serializable> deserializeObject(file: File): T {
            var ois: ObjectInputStream? = null
            try {
                ois = ObjectInputStream(FileInputStream(file))
                return ois.readObject() as T
            } finally {
                HttpIOUtils.closeQuietly(ois)
            }
        }
    }
}

class SerializableCookieHolder : Serializable {
    var map: Map<URI?, List<SerializableHttpCookie>>? = null

    companion object {
        private const val serialVersionUID = 42L
    }
}
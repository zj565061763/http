package com.sd.lib.http.utils

import android.text.TextUtils
import java.io.*

internal class HttpIOUtil private constructor() {

    companion object {
        @Throws(IOException::class)
        fun readString(inputStream: InputStream?, charset: String?): String {
            val tInputStream = if (inputStream is BufferedInputStream) inputStream else BufferedInputStream(inputStream)
            val tCharset = if (!TextUtils.isEmpty(charset)) charset else "UTF-8"

            val reader = InputStreamReader(tInputStream, tCharset)
            val sb = StringBuilder()
            val buffer = CharArray(1024)
            var len: Int
            while (reader.read(buffer).also { len = it } != -1) {
                sb.append(buffer, 0, len)
            }
            return sb.toString()
        }

        @Throws(IOException::class)
        fun writeString(outputStream: OutputStream?, content: String?, charset: String?) {
            val tCharset = if (!TextUtils.isEmpty(charset)) charset else "UTF-8"
            val writer = OutputStreamWriter(outputStream, tCharset)
            writer.write(content)
            writer.flush()
        }

        @JvmStatic
        @Throws(IOException::class)
        fun copy(inputStream: InputStream, outputStream: OutputStream, callback: ProgressCallback?) {
            val tInputStream = if (inputStream is BufferedInputStream) inputStream else BufferedInputStream(inputStream)
            val tOutputStream = if (outputStream is BufferedOutputStream) outputStream else BufferedOutputStream(outputStream)

            var count: Long = 0
            var len = 0
            val buffer = ByteArray(1024)
            while (tInputStream.read(buffer).also { len = it } != -1) {
                tOutputStream.write(buffer, 0, len)
                count += len.toLong()
                callback?.onProgress(count)
            }
            tOutputStream.flush()
        }

        @JvmStatic
        fun closeQuietly(closeable: Closeable?) {
            try {
                closeable?.close()
            } catch (ignored: Throwable) {
            }
        }
    }

    interface ProgressCallback {
        fun onProgress(count: Long)
    }
}
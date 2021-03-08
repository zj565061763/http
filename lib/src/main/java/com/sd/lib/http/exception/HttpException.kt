package com.sd.lib.http.exception

import java.io.PrintWriter
import java.io.StringWriter

open class HttpException : Exception {

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    /**
     * 堆栈信息
     */
    val stackTraceInfo by lazy {
        val printCause = cause
        if (printCause == null) {
            toString()
        } else {
            val stringWriter = StringWriter()
            val printWriter = PrintWriter(stringWriter)

            printWriter.println()
            printCause.printStackTrace(printWriter)
            printWriter.close()
            stringWriter.toString()
        }
    }

    override fun toString(): String {
        var superContent = super.toString()
        cause?.let {
            superContent = "$superContent \r\n $it"
        }
        return superContent
    }
}
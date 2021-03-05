package com.sd.lib.http.impl

import android.text.TextUtils
import com.sd.lib.http.ContentType
import com.sd.lib.http.IPostRequest
import com.sd.lib.http.IPostRequest.ParamsType
import com.sd.lib.http.IResponse
import com.sd.lib.http.body.*
import com.sd.lib.http.exception.HttpException
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection

class PostRequest : BaseRequestImpl(), IPostRequest {
    private var mBody: IRequestBody<*>? = null
    private var mListFile: MutableList<FilePart>? = null

    private val listFile: MutableList<FilePart> by lazy {
        val list: MutableList<FilePart> = ArrayList()
        mListFile = list
        list
    }

    override var paramsType: ParamsType = ParamsType.Default
        set(value) {
            requireNotNull(value)
            field = value
        }

    override fun addPart(name: String, file: File) {
        addPart(name, file, null, null)
    }

    override fun addPart(name: String, file: File, filename: String?, contentType: String?) {
        val filePart = FilePart(name, file, filename, contentType)
        listFile.add(filePart)
    }

    override fun setBody(body: IRequestBody<*>?) {
        mBody = body
    }

    @Throws(HttpException::class)
    override fun doExecute(): IResponse {
        val httpRequest = newHttpRequest(url, HttpRequest.METHOD_POST)

        val requestBody = mBody
        if (requestBody != null) {
            executeBody(httpRequest, requestBody)
        } else {
            when (paramsType) {
                ParamsType.Default -> executeDefault(httpRequest)
                ParamsType.Json -> executeJson(httpRequest)
            }
        }

        val response = Response(httpRequest)
        response.codeOrThrow
        return response
    }

    private fun executeBody(httpRequest: HttpRequest, requestBody: IRequestBody<*>) {
        httpRequest.contentType(requestBody.contentType)
        when (requestBody) {
            is StringBody -> {
                httpRequest.send(requestBody.body)
            }
            is FileBody -> {
                httpRequest.send(requestBody.body)
            }
            is BytesBody -> {
                httpRequest.send(requestBody.body)
            }
        }
    }

    private fun executeDefault(httpRequest: HttpRequest) {
        val map = params.toMap()
        val list = mListFile
        if (list == null || list.isEmpty()) {
            httpRequest.form(map)
        } else {
            for ((key, value) in map) {
                httpRequest.part(key, value.toString())
            }
            for (item in list) {
                httpRequest.part(item.name, item.filename, item.contentType, item.file)
            }
        }
    }

    private fun executeJson(httpRequest: HttpRequest) {
        val jsonObject = JSONObject()

        val map = params.toMap()
        for ((key, value) in map) {
            jsonObject.put(key, value)
        }

        val json = jsonObject.toString()
        val requestBody = JsonBody(json)
        executeBody(httpRequest, requestBody)
    }

    private class FilePart(name: String, file: File?, filename: String?, contentType: String?) {
        val name: String
        val filename: String?
        val contentType: String?
        val file: File

        init {
            var filename = filename
            var contentType = contentType
            require(!TextUtils.isEmpty(name)) { "name is empty" }
            requireNotNull(file) { "file is null" }
            if (TextUtils.isEmpty(contentType)) {
                contentType = HttpURLConnection.guessContentTypeFromName(file.name)
                if (TextUtils.isEmpty(contentType)) contentType = ContentType.STREAM
            }
            if (TextUtils.isEmpty(filename)) filename = file.name
            this.name = name
            this.filename = filename
            this.contentType = contentType
            this.file = file
        }
    }
}
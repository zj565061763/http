package com.sd.lib.http.impl

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
    private var _body: IRequestBody<*>? = null
    private var _listFile: MutableList<FilePart>? = null

    private val listFile: MutableList<FilePart> by lazy {
        mutableListOf<FilePart>().also {
            _listFile = it
        }
    }

    override var paramsType: ParamsType = ParamsType.Default

    override fun addPart(name: String, file: File) {
        addPart(name, file, null, null)
    }

    override fun addPart(name: String, file: File, filename: String?, contentType: String?) {
        val filePart = FilePart(name, file, filename, contentType)
        listFile.add(filePart)
    }

    override fun setBody(body: IRequestBody<*>?) {
        _body = body
    }

    override fun doExecute(): IResponse {
        val httpRequest = newHttpRequest(url, HttpRequest.METHOD_POST)

        try {
            val requestBody = _body
            if (requestBody != null) {
                executeBody(requestBody, httpRequest)
            } else {
                when (paramsType) {
                    ParamsType.Default -> executeDefault(httpRequest)
                    ParamsType.Json -> executeJson(httpRequest)
                }
            }
        } catch (e: HttpRequest.HttpRequestException) {
            throw e.cause ?: e
        }

        return Response(httpRequest)
    }

    private fun executeBody(requestBody: IRequestBody<*>, httpRequest: HttpRequest) {
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
            else -> throw HttpException("unknown request body:${requestBody}")
        }
    }

    private fun executeDefault(httpRequest: HttpRequest) {
        val map = params.toMap()

        val list = _listFile
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
        executeBody(requestBody, httpRequest)
    }

    private class FilePart {
        val name: String
        val filename: String
        val contentType: String
        val file: File

        constructor(name: String, file: File, filename: String?, contentType: String?) {
            require(name.isNotEmpty()) { "name is empty" }

            this.name = name
            this.file = file
            this.filename = if (filename == null || filename.isEmpty()) file.name else filename

            var type = contentType
            if (type == null || type.isEmpty()) type = HttpURLConnection.guessContentTypeFromName(file.name)
            if (type == null || type.isEmpty()) type = ContentType.STREAM

            this.contentType = type
        }
    }
}
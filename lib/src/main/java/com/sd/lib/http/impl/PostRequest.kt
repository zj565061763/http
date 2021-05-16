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

        val requestBody = _body
        if (requestBody != null) {
            executeBody(requestBody, httpRequest)
        } else {
            when (paramsType) {
                ParamsType.Default -> executeDefault(httpRequest)
                ParamsType.Json -> executeJson(httpRequest)
            }
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
        val filename: String?
        val contentType: String?
        val file: File

        constructor(name: String, file: File, filename: String?, contentType: String?) {
            require(!TextUtils.isEmpty(name)) { "name is empty" }
            requireNotNull(file) { "file is null" }

            this.name = name
            this.file = file
            this.filename = if (TextUtils.isEmpty(filename)) file.name else filename

            var cType = contentType
            if (TextUtils.isEmpty(cType)) {
                cType = HttpURLConnection.guessContentTypeFromName(file.name)
                if (TextUtils.isEmpty(cType)) cType = ContentType.STREAM
            }
            this.contentType = cType
        }
    }
}
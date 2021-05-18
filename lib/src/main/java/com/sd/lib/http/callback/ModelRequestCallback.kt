package com.sd.lib.http.callback

import com.sd.lib.http.IResponse
import com.sd.lib.http.exception.HttpExceptionParseResponse
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class ModelRequestCallback<T> : RequestCallback() {
    var actModel: T? = null
        private set

    @Throws(Exception::class)
    override fun onSuccessBackground(response: IResponse) {
        super.onSuccessBackground(response)
        val type = modelType
        val content = response.readString()
        try {
            actModel = parseToModel(content, type)
        } catch (e: Exception) {
            throw HttpExceptionParseResponse(cause = e)
        }
    }

    protected val modelType: Type
        get() {
            val parameterizedType = javaClass.genericSuperclass as ParameterizedType
            val types = parameterizedType.actualTypeArguments
            return if (types != null && types.isNotEmpty()) {
                types[0]
            } else {
                throw RuntimeException("generic type not found")
            }
        }

    /**
     * 将字符串[content]解析为实体
     */
    @Throws(Exception::class)
    protected abstract fun parseToModel(content: String, type: Type): T
}
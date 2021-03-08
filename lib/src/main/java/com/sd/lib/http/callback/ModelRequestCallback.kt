package com.sd.lib.http.callback

import androidx.annotation.CallSuper
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class ModelRequestCallback<T> : StringRequestCallback() {
    var actModel: T? = null
        private set

    @CallSuper
    @Throws(Exception::class)
    override fun onSuccessBackground() {
        super.onSuccessBackground()
        val type = modelType
        actModel = parseToModel(result, type)
    }

    protected val modelType: Type
        protected get() {
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
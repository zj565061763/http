package com.sd.lib.http.target

import com.google.gson.Gson
import com.sd.lib.http.IRequest
import com.sd.lib.http.exception.HttpException
import com.sd.lib.http.exception.HttpExceptionResponseCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

class DefaultHttpFuture<T> : IHttpFuture<T> {

    private val mTargetClass: Class<T>
    private val mRequest: IRequest

    private var mResult: IHttpFuture.Result? = null

    @Volatile
    private var mTarget: T? = null

    @Volatile
    private var mException: HttpException? = null

    internal constructor(targetClass: Class<T>, request: IRequest) {
        this.mTargetClass = targetClass
        this.mRequest = request
    }

    override val target: T?
        get() = mTarget

    override val exception: HttpException?
        get() = mException

    @Synchronized
    override suspend fun execute(): IHttpFuture.Result {
        val result = mResult
        if (result != null) return result

        try {
            withContext(Dispatchers.IO) {
                val response = mRequest.execute()

                val codeException = HttpExceptionResponseCode.from(response.code)
                if (codeException != null) throw codeException

                val content = response.asString
                if (!isActive) return@withContext

                if (mTargetClass == String::class.java) {
                    mTarget = content as T
                } else {
                    mTarget = Gson().fromJson(content, mTargetClass)
                }

                requireNotNull(mTarget)
                mResult = IHttpFuture.Result.Success
            }
        } catch (e: Exception) {
            if (e is CancellationException) {
                mResult = IHttpFuture.Result.Cancel
            } else {
                mException = HttpException.wrap(e)
                mResult = IHttpFuture.Result.Error
            }
        }

        return mResult!!
    }
}
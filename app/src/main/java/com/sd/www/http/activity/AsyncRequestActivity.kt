package com.sd.www.http.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.sd.lib.http.IRequest
import com.sd.lib.http.RequestHandler
import com.sd.lib.http.RequestManager
import com.sd.lib.http.callback.ModelRequestCallback
import com.sd.lib.http.impl.GetRequest
import com.sd.www.http.databinding.ActivityAsyncRequestBinding
import com.sd.www.http.model.WeatherModel
import java.lang.reflect.Type
import java.util.*

/**
 * 异步请求demo
 */
class AsyncRequestActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = AsyncRequestActivity::class.java.simpleName
    private val URL = "http://www.weather.com.cn/data/cityinfo/101010100.html"

    private lateinit var mBinding: ActivityAsyncRequestBinding
    private val mRequestHandlers: MutableMap<RequestHandler, String> = WeakHashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAsyncRequestBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onClick(v: View) {
        if (v == mBinding.btnStart) {
            requestData()
        } else if (v == mBinding.btnCancel) {
            cancelRequest()
        }
    }

    /**
     * 发起请求
     */
    private fun requestData() {
        val request = GetRequest()
        // 设置请求地址
        request.baseUrl = URL
        // 设置请求参数
        request.params.put("aaa", "aaa").put("bbb", "bbb")
        // 设置该请求的tag，可用于取消请求
        request.tag = TAG

        // 发起异步请求
        val requestHandler = request.execute(mModelRequestCallback)

        // 保存请求处理对象，用于取消
        mRequestHandlers[requestHandler] = ""
    }

    /**
     * 回调对象
     */
    private val mModelRequestCallback = object : ModelRequestCallback<WeatherModel>() {

        override fun onPrepare(request: IRequest) {
            super.onPrepare(request)
            // 请求在执行之前的准备回调（发起请求的线程）
            Log.i(TAG, "onPrepare----->")
        }

        override fun onStart() {
            super.onStart()
            // 开始回调（UI线程）
            Log.i(TAG, "onStart")
        }

        @Throws(Exception::class)
        override fun onSuccessBackground() {
            super.onSuccessBackground()
            // 成功回调，super里面回调了parseToModel方法把返回的内容转为实体（非UI线程）
            Log.i(TAG, "onSuccessBackground")
        }

        @Throws(Exception::class)
        override fun parseToModel(content: String, type: Type): WeatherModel {
            // 把返回的内容转实体（非UI线程）
            return Gson().fromJson(content, type)
        }

        override fun onSuccessBefore() {
            // 成功回调（UI线程）
            Log.i(TAG, "onSuccessBefore")
        }

        override fun onSuccess() {
            // 成功回调（UI线程）

            // 获得接口对应的实体
            val model = actModel!!
            Log.i(TAG, "onSuccess code:${response.code} city:${model.weatherinfo!!.city}")
        }

        override fun onError(e: Exception) {
            super.onError(e)
            // 异常回调，请求异常或者成功之后的数据处理异常（UI线程）
            Log.i(TAG, "onError:$e")
        }

        override fun onCancel() {
            super.onCancel()
            // 取消回调（UI线程）
            Log.i(TAG, "onCancel")
        }

        override fun onFinish() {
            super.onFinish()
            // 结束回调（UI线程）
            Log.i(TAG, "onFinish")
        }
    }

    /**
     * 取消请求
     */
    private fun cancelRequest() {
        mRequestHandlers.forEach {
            it.key.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        // 根据tag取消请求
        RequestManager.instance.cancelTag(TAG)
    }
}
package com.sd.www.http.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.sd.lib.http.IRequest
import com.sd.lib.http.RequestManager
import com.sd.lib.http.callback.ModelRequestCallback
import com.sd.lib.http.impl.GetRequest
import com.sd.www.http.databinding.ActivityAsyncRequestBinding
import com.sd.www.http.model.WeatherModel
import java.lang.reflect.Type

/**
 * 异步请求demo
 */
class AsyncRequestActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = AsyncRequestActivity::class.java.simpleName
    private val URL = "http://www.weather.com.cn/data/cityinfo/101010100.html"

    private lateinit var _binding: ActivityAsyncRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAsyncRequestBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }

    override fun onClick(v: View) {
        when (v) {
            _binding.btnStart -> requestData()
            _binding.btnCancel -> cancelRequest()
        }
    }

    /**
     * 发起请求
     */
    private fun requestData() {
        val request = GetRequest().apply {
            // 设置请求地址
            this.baseUrl = URL
            // 设置请求参数
            this.params.put("aaa", "aaa")
            // 设置该请求的tag，可用于取消请求
            this.tag = TAG
        }

        // 发起异步请求
        request.execute(_requestCallback)
    }

    /**
     * 回调对象
     */
    private val _requestCallback = object : ModelRequestCallback<WeatherModel>() {

        override fun onPrepare(request: IRequest) {
            super.onPrepare(request)
            // 请求在执行之前的准备回调（发起请求的线程）
            Log.i(TAG, "onPrepare-----> ${Thread.currentThread()}")
        }

        override fun onStart() {
            super.onStart()
            // 开始回调（UI线程）
            Log.i(TAG, "onStart")
        }

        override fun parseToModel(content: String, type: Type): WeatherModel {
            // 把返回的内容转实体（非UI线程）
            Log.i(TAG, "parseToModel content:${content} type:${type} ${Thread.currentThread()}")
            return Gson().fromJson(content, type)
        }

        override fun onSuccessBefore() {
            // 成功回调（UI线程）
            Log.i(TAG, "onSuccessBefore ${Thread.currentThread()}")
        }

        override fun onSuccess() {
            // 成功回调（UI线程）

            // 获得接口对应的实体
            val model = actModel!!
            Log.i(TAG, "onSuccess code:${httpResponse.code} city:${model.weatherinfo!!.city} ${Thread.currentThread()}")
        }

        override fun onError(e: Exception) {
            super.onError(e)
            // 异常回调，请求异常或者成功之后的数据处理异常（UI线程）
            Log.i(TAG, "onError:${e} ${Thread.currentThread()}")
        }

        override fun onCancel() {
            super.onCancel()
            // 取消回调（UI线程）
            Log.i(TAG, "onCancel ${Thread.currentThread()}")
        }

        override fun onFinish() {
            super.onFinish()
            // 结束回调（UI线程）
            Log.i(TAG, "onFinish ${Thread.currentThread()}")
        }
    }

    /**
     * 取消请求
     */
    private fun cancelRequest() {
        // 根据tag取消请求
        RequestManager.instance.cancelTag(TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        cancelRequest()
    }
}
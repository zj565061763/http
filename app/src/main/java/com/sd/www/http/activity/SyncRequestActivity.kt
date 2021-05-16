package com.sd.www.http.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.http.exception.HttpExceptionCancellation
import com.sd.lib.http.exception.HttpExceptionParseResponse
import com.sd.lib.http.exception.HttpExceptionResponseCode
import com.sd.lib.http.exception.HttpExceptionResultIntercepted
import com.sd.lib.http.impl.GetRequest
import com.sd.www.http.databinding.ActivitySyncRequestBinding
import com.sd.www.http.model.WeatherModel
import kotlinx.coroutines.*

/**
 * 同步请求demo
 */
class SyncRequestActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _binding: ActivitySyncRequestBinding

    val _mainScope: CoroutineScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySyncRequestBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }

    override fun onClick(v: View?) {
        if (v == _binding.btnStart) {
            requestData()
        }
    }

    private fun requestData() {
        _mainScope.launch {
            val request = GetRequest().apply {
                this.baseUrl = "http://www.weather.com.cn/data/cityinfo/101010100.html"
                this.extra = "SyncRequestActivity"
            }

            val result = withContext(Dispatchers.IO) {
                request.parse(WeatherModel::class.java) {
                    // 返回true取消请求
                    !isActive
                }
            }

            if (result.isSuccess) {
                _binding.tvResult.text = result.data!!.weatherinfo!!.city
            } else {
                val desc = when (val failure = result.failure) {
                    is HttpExceptionResponseCode -> {
                        "服务器异常，错误码:${failure.code}"
                    }
                    is HttpExceptionParseResponse -> {
                        "数据解析异常:${failure}"
                    }
                    is HttpExceptionCancellation -> {
                        "请求被取消"
                    }
                    is HttpExceptionResultIntercepted -> {
                        "请求被拦截"
                    }
                    else -> result.failure.toString()
                }
                _binding.tvResult.text = desc
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _mainScope.cancel()
    }
}
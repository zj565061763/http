package com.sd.www.http.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.http.exception.HttpException
import com.sd.lib.http.exception.HttpExceptionCancellation
import com.sd.lib.http.exception.HttpExceptionResultIntercepted
import com.sd.lib.http.impl.GetRequest
import com.sd.www.http.databinding.ActivitySyncRequestBinding
import com.sd.www.http.model.WeatherModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

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

            val result = request.parseSuspend(WeatherModel::class.java)
            if (result.isSuccess) {
                _binding.tvResult.text = result.data!!.weatherinfo!!.city
            } else {
                val desc = when (val exception = result.exception) {
                    is HttpExceptionCancellation -> "请求被取消"
                    is HttpExceptionResultIntercepted -> "请求结果被拦截"
                    is HttpException -> exception.getDescFormat(application)
                    else -> exception.toString()
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
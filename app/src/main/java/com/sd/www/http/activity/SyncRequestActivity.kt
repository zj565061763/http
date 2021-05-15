package com.sd.www.http.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
            }

            val result = withContext(Dispatchers.IO) {
                request.parse(WeatherModel::class.java)
            }

            if (result.isSuccess) {
                _binding.tvResult.text = result.data!!.weatherinfo!!.city
            } else {
                _binding.tvResult.text = result.failure.toString()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _mainScope.cancel()
    }
}
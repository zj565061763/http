package com.sd.www.http.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.http.impl.GetRequest
import com.sd.lib.http.target.IHttpFuture
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
    private lateinit var mBinding: ActivitySyncRequestBinding

    val mMainScope: CoroutineScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySyncRequestBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onClick(v: View?) {
        if (v == mBinding.btnStart) {
            requestData()
        }
    }

    private fun requestData() {
        mMainScope.launch {
            // 创建请求对象
            val request = GetRequest()
            // 设置请求地址
            request.baseUrl = "http://www.weather.com.cn/data/cityinfo/101010100.html"
            // 转换
            val future = request.future(WeatherModel::class.java)
            // 发起请求，返回请求结果
            val result = future.execute()
            when (result) {
                IHttpFuture.Result.Success -> {
                    future.target!!.let {
                        mBinding.tvResult.text = "请求成功：${it.weatherinfo!!.city}"
                    }
                }
                IHttpFuture.Result.Error -> future.exception!!.let {
                    mBinding.tvResult.text = "请求异常：${it}"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMainScope.cancel()
    }
}
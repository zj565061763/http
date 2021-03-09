package com.sd.www.http.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.http.impl.GetRequest
import com.sd.www.http.databinding.ActivitySyncRequestBinding
import kotlinx.coroutines.*

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
            val content = getContent()
            mBinding.tvResult.text = content
        }
    }

    private suspend fun getContent(): String {
        return withContext(Dispatchers.IO) {
            // 创建请求对象
            val request = GetRequest()
            // 设置请求地址
            request.baseUrl = "https://www.baidu.com/"
            // 发起请求，得到Response对象
            val response = request.execute()
            // 请求结果以字符串返回
            response.asString
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMainScope.cancel()
    }
}
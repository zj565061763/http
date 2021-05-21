package com.sd.demo.http.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.http.RequestManager
import com.sd.lib.http.cookie.store.SerializableCookieStore
import com.sd.demo.http.databinding.ActivityMainBinding
import com.sd.demo.http.utils.AppRequestInterceptor
import com.sd.demo.http.utils.AppResponseParser
import com.sd.demo.http.utils.AppResultInterceptor

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        // 调试模式，输出log
        RequestManager.isDebug = true

        // cookie管理对象
        RequestManager.cookieStore = SerializableCookieStore(this)

        // 请求拦截
        RequestManager.requestInterceptor = AppRequestInterceptor()

        // http返回解析器
        RequestManager.responseParser = AppResponseParser()

        // 结果拦截器
        RequestManager.resultInterceptor = AppResultInterceptor()
    }

    override fun onClick(v: View) {
        when (v) {
            _binding.btnAsyncRequestActivity -> startActivity(Intent(this, AsyncRequestActivity::class.java))
            _binding.btnSyncRequestActivity -> startActivity(Intent(this, SyncRequestActivity::class.java))
            _binding.btnDownloadActivity -> startActivity(Intent(this, DownloadActivity::class.java))
        }
    }
}
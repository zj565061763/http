package com.sd.www.http.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.http.RequestManager
import com.sd.lib.http.cookie.store.SerializableCookieStore
import com.sd.www.http.cookie.InMemoryCookieStore
import com.sd.www.http.databinding.ActivityMainBinding
import com.sd.www.http.utils.AppRequestInterceptor
import com.sd.www.http.utils.AppResponseParser
import com.sd.www.http.utils.AppResultInterceptor
import java.net.URI

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        // 调试模式，输出log
        RequestManager.instance.isDebug = true

        // cookie管理对象
        RequestManager.instance.cookieStore = SerializableCookieStore(this)

        // 请求拦截
        RequestManager.instance.requestInterceptor = AppRequestInterceptor()

        // http返回解析器
        RequestManager.instance.responseParser = AppResponseParser()

        // 结果拦截器
        RequestManager.instance.resultInterceptor = AppResultInterceptor()

        testCookie()
    }

    override fun onClick(v: View) {
        when (v) {
            _binding.btnAsyncRequestActivity -> startActivity(Intent(this, AsyncRequestActivity::class.java))
            _binding.btnSyncRequestActivity -> startActivity(Intent(this, SyncRequestActivity::class.java))
            _binding.btnDownloadActivity -> startActivity(Intent(this, DownloadActivity::class.java))
        }
    }

    private fun testCookie() {

        val manager = java.net.CookieManager(InMemoryCookieStore(), null)

        val uri = URI.create("http://foo.com/hello")
        val listCookie = mutableListOf("session=1111111111", "userId=aaa")
        val header = mutableMapOf<String, List<String>>("Set-Cookie" to listCookie)
        manager.put(uri, header)

        val resultUri = URI.create("http://bar.foo.com/world")
        val resultHeader = manager.get(resultUri, mutableMapOf<String, List<String>>())

        Log.i(MainActivity::class.java.simpleName, resultHeader.toString())
    }
}
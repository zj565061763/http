package com.sd.www.http.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.http.RequestManager
import com.sd.lib.http.cookie.SerializableCookieStore
import com.sd.www.http.databinding.ActivityMainBinding
import com.sd.www.http.utils.AppRequestInterceptor
import com.sd.www.http.utils.AppResponseParser

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //设置调试模式，输出log
        RequestManager.instance.isDebug = true

        //设置cookie管理对象
        RequestManager.instance.cookieStore = SerializableCookieStore(this)

        //设置请求拦截对象，可用于log输出，或者一些需要全局处理的逻辑，注意这边传入的对象如果是和资源相关的对象，需要在资源销毁的时候remove
        RequestManager.instance.requestInterceptor = AppRequestInterceptor()

        // 设置返回结果解析
        RequestManager.instance.responseParser = AppResponseParser()
    }

    override fun onClick(v: View) {
        if (v == mBinding.btnAsyncRequestActivity) {
            startActivity(Intent(this, AsyncRequestActivity::class.java))
        } else if (v == mBinding.btnSyncRequestActivity) {
            startActivity(Intent(this, SyncRequestActivity::class.java))
        } else if (v == mBinding.btnDownloadActivity) {
            startActivity(Intent(this, DownloadActivity::class.java))
        }
    }
}
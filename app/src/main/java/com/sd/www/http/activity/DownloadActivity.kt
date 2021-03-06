package com.sd.www.http.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.http.Request
import com.sd.lib.http.RequestManager
import com.sd.lib.http.callback.FileRequestCallback
import com.sd.lib.http.impl.GetRequest
import com.sd.lib.http.utils.TransmitParam
import com.sd.www.http.databinding.ActivityDownloadBinding
import java.io.File

class DownloadActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = DownloadActivity::class.java.simpleName
    private val URL_FILE = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk"

    private lateinit var mBinding: ActivityDownloadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }

    override fun onClick(v: View) {
        if (v === mBinding.btnStart) {
            startDownload()
        } else if (v === mBinding.btnStop) {
            stopDownload()
        }
    }

    private fun startDownload() {
        val file = File(externalCacheDir, "download.apk")

        val request: Request = GetRequest()
        request.baseUrl = URL_FILE
        request.tag = TAG
        request.execute(object : FileRequestCallback(file) {
            override fun onProgressDownload(param: TransmitParam) {
                mBinding.progressBar.progress = param.progress
                mBinding.tvProgress.text = "${param.progress}%"
                mBinding.tvSpeed.text = "${param.speedKBps}KB/秒"
            }

            override fun onSuccess() {
                Log.i(TAG, "download finish")
            }

            override fun onError(e: Exception) {
                super.onError(e)
                Log.i(TAG, "download error:$e")
            }

            override fun onCancel() {
                super.onCancel()
                mBinding.tvSpeed.text = ""
                Log.i(TAG, "download cancelled")
            }
        })
    }

    private fun stopDownload() {
        RequestManager.getInstance().cancelTag(TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopDownload()
    }
}
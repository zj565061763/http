package com.sd.example.http.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.sd.lib.http.Request
import com.sd.lib.http.RequestManager
import com.sd.lib.http.callback.FileRequestCallback
import com.sd.lib.http.impl.GetRequest
import com.sd.lib.http.utils.TransmitParam
import com.sd.example.http.databinding.ActivityDownloadBinding
import java.io.File

class DownloadActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = DownloadActivity::class.java.simpleName
    private val URL_FILE = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk"

    private lateinit var _binding: ActivityDownloadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }

    override fun onClick(v: View) {
        when (v) {
            _binding.btnStart -> startDownload()
            _binding.btnStop -> stopDownload()
        }
    }

    private fun startDownload() {
        val request: Request = GetRequest().apply {
            this.baseUrl = URL_FILE
            this.tag = TAG
        }

        val file = File(externalCacheDir, "download.apk")
        request.execute(object : FileRequestCallback(file) {

            override fun onStart() {
                super.onStart()
                Log.i(TAG, "onStart")
            }

            override fun onProgressDownload(param: TransmitParam) {
                _binding.progressBar.progress = param.progress
                _binding.tvProgress.text = "${param.progress}%"
                _binding.tvSpeed.text = "${param.speedKBps}KB/秒"
            }

            override fun onSuccess() {
                Log.i(TAG, "onSuccess")
            }

            override fun onError(e: Exception) {
                super.onError(e)
                Log.i(TAG, "onError:$e")
            }

            override fun onCancel() {
                super.onCancel()
                _binding.progressBar.progress = 0
                _binding.tvProgress.text = ""
                _binding.tvSpeed.text = ""
                Log.i(TAG, "onCancel")
            }
        })
    }

    private fun stopDownload() {
        RequestManager.cancelTag(TAG)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopDownload()
    }
}
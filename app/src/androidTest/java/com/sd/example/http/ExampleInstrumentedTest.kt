package com.example.result

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sd.example.http.utils.Lifecycle
import com.sd.example.http.utils.LifecycleRequestCallback
import com.sd.lib.http.impl.GetRequest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    val GET_REQUEST = GetRequest().apply {
        this.baseUrl = "http://www.weather.com.cn/data/cityinfo/101010100.html"
    }

    @Test
    fun testNormal() {
        val lifecycleRequestCallback = object : LifecycleRequestCallback() {
            override fun onFinish() {
                super.onFinish()
                checkLifecycle(
                    Lifecycle.onPrepare,
                    Lifecycle.onStart,
                    Lifecycle.onResponseBackground,
                    Lifecycle.onSuccessBefore,
                    Lifecycle.onSuccess,
                    Lifecycle.onFinish,
                )
            }
        }
        GET_REQUEST.execute(lifecycleRequestCallback)
    }

    @Test
    fun testCancel_onStart() {
        val lifecycleRequestCallback = object : LifecycleRequestCallback() {
            override fun onStart() {
                super.onStart()
                httpRequestHandler.cancel()
            }

            override fun onFinish() {
                super.onFinish()
                checkLifecycle(
                    Lifecycle.onPrepare,
                    Lifecycle.onStart,
                    Lifecycle.onCancel,
                    Lifecycle.onFinish,
                )
            }
        }
        GET_REQUEST.execute(lifecycleRequestCallback)
    }
}
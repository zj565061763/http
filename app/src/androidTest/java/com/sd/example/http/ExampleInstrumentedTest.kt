package com.sd.example.http

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sd.example.http.utils.*
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
        GET_REQUEST.execute(callbackNormal)
    }

    @Test
    fun testCancel_onStart() {
        GET_REQUEST.execute(callbackCancel_onStart)
    }

    @Test
    fun testCancel_onResponseBackground() {
        GET_REQUEST.execute(callbackCancel_onResponseBackground)
    }

    @Test
    fun testCancel_onSuccess() {
        GET_REQUEST.execute(callbackCancel_onSuccess)
    }

    @Test
    fun testCancel_onError() {
        GET_REQUEST.execute(callbackCancel_onError)
    }

    @Test
    fun testException_onResponseBackground() {
        GET_REQUEST.execute(callbackException_onResponseBackground)
    }
}
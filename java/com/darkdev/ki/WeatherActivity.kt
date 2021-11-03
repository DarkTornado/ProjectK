package com.darkdev.ki

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.getStringExtra("data")
        val pos = intent.getStringExtra("pos")
        if (data == null) {
            toast("비정상적인 실행이 감지되었어요 :(")
            finish()
        }
        supportActionBar?.hide()

        val web = WebView(this)
        web.loadUrl("file:///android_asset/weather.html")
        web.settings.javaScriptEnabled = true
        web.webChromeClient = WebChromeClient()
        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.loadUrl("javascript:applyWeatherInfo('$data', '$pos')")
                super.onPageFinished(view, url)
            }
        }
        web.layoutParams = LinearLayout.LayoutParams(-1, -1)

        setContentView(web)
    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

}
package com.darkdev.ki

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class WebActivity : AppCompatActivity() {

    var web: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        web?.settings?.javaScriptEnabled = true
        web?.settings?.builtInZoomControls = true
        val url = intent.data.toString()
        web?.loadUrl(url)
        setContentView(web);
    }

}
package com.darkdev.ki

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity

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

    override fun onBackPressed() {
        if (web!!.canGoBack()) web!!.goBack() else finish()
    }

}

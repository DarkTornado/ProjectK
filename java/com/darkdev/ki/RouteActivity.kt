package com.darkdev.ki

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception

class RouteActivity : AppCompatActivity() {

    var web: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val intent = intent
            val dest = intent.getStringExtra("dest")
            val route = intent.getStringExtra("route")
            supportActionBar?.title = "길찾기 : 현위치 → $dest"

            val layout = LinearLayout(this)
            layout.orientation = 1
            web = WebView(this)
            val webSet = web!!.settings
            webSet.builtInZoomControls = true
            web!!.loadUrl("file:///android_asset/route.html")
            web!!.settings.javaScriptEnabled = true
            web!!.webChromeClient = WebChromeClient()
            web!!.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    web!!.loadUrl("javascript:applyRoute('$route')")
                    super.onPageFinished(view, url)
                }
            }
            web!!.layoutParams = LinearLayout.LayoutParams(-1, -1)
            layout.addView(web)
            setContentView(layout)
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}
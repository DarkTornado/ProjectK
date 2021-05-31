package com.darkdev.ki

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SubwayActivity : AppCompatActivity() {

    var web: WebView? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menus = listOf("수도권", "부산", "대전", "대구", "광주")
        for (n in menus.indices) {
            menu.add(0, n, 0, menus[n]).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menus = listOf("seoul", "busan", "daejeon", "daegu", "gwangju")
        val scales = listOf(100, 100, 1, 1, 1)
        web!!.loadUrl("file:///android_asset/subway_map/" + menus[item.itemId] + ".html")
        web!!.setInitialScale(scales[item.itemId])
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        web = WebView(this)
        web!!.loadUrl("file:///android_asset/subway_map/seoul.html")
        web!!.webChromeClient = WebChromeClient()
        web!!.webViewClient = WebViewClient()
        web!!.setInitialScale(100)

        val webSettings = web!!.settings
        webSettings.useWideViewPort = true
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = true
        webSettings.loadWithOverviewMode = true
        webSettings.displayZoomControls = false
        webSettings.setSupportZoom(true)

        setContentView(web!!)
    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    fun dip2px(dips: Int) = Math.ceil(dips * this.resources.displayMetrics.density.toDouble()).toInt()

}

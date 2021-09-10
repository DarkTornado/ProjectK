package com.darkdev.ki

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
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
        web!!.addJavascriptInterface(JSLinker(this), "android")

        val webSettings = web!!.settings
        webSettings.useWideViewPort = true
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = true
        webSettings.loadWithOverviewMode = true
        webSettings.displayZoomControls = false
        webSettings.allowUniversalAccessFromFileURLs = true;
        webSettings.setSupportZoom(true)
        setContentView(web)
    }

    fun openStationMenu(data: String?) {
        val intent = Intent(this, WebActivity::class.java)
        intent.data = Uri.parse("https://m.search.naver.com/search.naver?query=" + data + "역 전철 시간표")
        intent.putExtra("title", data+"역 전철 시간표")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private class JSLinker(private val act: SubwayActivity) {
        @JavascriptInterface
        fun stationInfo(msg: String?, pos: Int) {
            Handler().post {
                var data = msg;
                if (pos == 1) {
                    when (msg) {
                        "신촌(지하)" -> data = "2호선 신촌"
                        "신촌" -> data = "경의중앙선 신촌"
                        "양평(KTX)" -> data = "경의중앙선 양평"
                        "평택지제" -> data = "지제"
                        "시우" -> data = "원곡"
                    }
                }
                act.openStationMenu(data)
            }
        }
    }

}
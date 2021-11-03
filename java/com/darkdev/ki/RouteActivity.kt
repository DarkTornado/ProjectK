package com.darkdev.ki

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RouteActivity : AppCompatActivity() {

    var web: WebView? = null
    var dest: String? = null
    var start: LocationSaver? = null
    var end: LocationSaver? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Thread {
            val route = Utils.findRoute(start, end, dest)
            runOnUiThread { web!!.loadUrl("javascript:applyRoute('$route')") }
        }.start()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 0, 0, "새로 고침").setIcon(R.drawable.reload).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val intent = intent
            val route = intent.getStringExtra("route")
            dest = intent.getStringExtra("dest")
            start = intent.getSerializableExtra("srmtart") as LocationSaver?
            end = intent.getSerializableExtra("end") as LocationSaver?
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
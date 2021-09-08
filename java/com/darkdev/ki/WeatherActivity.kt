package com.darkdev.ki

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.darkdev.uilib.BottomNavigationLayout

class WeatherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.getStringExtra("data")
        val pos = intent.getStringExtra("pos")
        val loc = intent.getStringExtra("loc")
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

    fun getRipple(): Drawable? {
        val color = Color.parseColor("#81D4FA")
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RippleDrawable(ColorStateList.valueOf(Color.WHITE), ColorDrawable(color), null)
        } else {
            ColorDrawable(color)
        }
    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

}
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
        supportActionBar?.title = "날씨 : $pos"

        val web = arrayOfNulls<WebView>(3)
        for (n in 0..2) {
            web[n] = WebView(this)
            web[n]?.loadUrl("file:///android_asset/weather.html")
            web[n]?.settings?.javaScriptEnabled = true
            web[n]?.webChromeClient = WebChromeClient()
            web[n]?.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    view.loadUrl("javascript:applyWeatherInfo($data, '$loc', $n)")
                    super.onPageFinished(view, url)
                }
            }
            web[n]?.setBackgroundColor(Color.WHITE)
            web[n]?.layoutParams = LinearLayout.LayoutParams(-1, -1)
        }

        val layout = BottomNavigationLayout(this)
        layout.setBackgroundColor(Color.WHITE)
        layout.setBottomBackgroundColor(Color.parseColor("#81D4FA"))
        layout.addView(web[0]);

        layout.addBottomButton("오늘", R.drawable.today, getRipple(), {
            layout.replace(web[0])
        }, 12f, Color.parseColor("#01579B"))

        layout.addBottomButton("내일", R.drawable.tomo, getRipple(), {
            layout.replace(web[1])
        }, 12f, Color.parseColor("#01579B"))

        layout.addBottomButton("모래", R.drawable.tomo2, getRipple(), {
            layout.replace(web[2])
        }, 12f, Color.parseColor("#01579B"))

        setContentView(layout)
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

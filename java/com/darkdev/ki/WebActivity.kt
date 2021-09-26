package com.darkdev.ki

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class WebActivity : AppCompatActivity() {

    var web: WebView? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menus = listOf("뒤로", "앞으로", "새로고침", "URL 설정", "종료")
        for (n in menus.indices) {
            menu.add(0, n, 0, menus[n]).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> if (web!!.canGoBack()) web!!.goBack()
            1 -> if (web!!.canGoForward()) web!!.goForward()
            2 -> web!!.reload()
            3 -> inputUrl()
            4 -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = intent.getStringExtra("title")
        if (title != null) supportActionBar!!.setTitle(title)

        web = WebView(this)
        web!!.webChromeClient = WebChromeClient()
        web!!.webViewClient = WebViewClient()
        web!!.settings!!.javaScriptEnabled = true
        web!!.settings.builtInZoomControls = true

        val url = intent.data.toString()
        web?.loadUrl(url)

        setContentView(web);
    }

    fun inputUrl() {
        val dialog = AlertDialog.Builder(this)
        val layout = LinearLayout(this)
        layout.orientation = 1
        val txt1 = TextView(this)
        txt1.text = "URL : "
        txt1.textSize = 18f
        txt1.setTextColor(Color.BLACK)
        layout.addView(txt1)
        val txt2 = EditText(this)
        txt2.hint = "URL을 입력하세요..."
        txt2.setText(web!!.url)
        txt2.isSingleLine = true
        layout.addView(txt2)
        val pad: Int = dip2px(10)
        layout.setPadding(pad, pad, pad, pad)
        val scroll = ScrollView(this)
        scroll.addView(layout)
        dialog.setView(scroll)
        dialog.setTitle("URL 입력")
        dialog.setNegativeButton("취소", null)
        dialog.setNeutralButton("URL 복사") { _dialog, which ->
            val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("label", txt2.text.toString()))
        }
        dialog.setPositiveButton("확인") { _dialog, whichButton ->
            val url = txt2.text.toString()
            if (url.isBlank()) {
                toast("URL이 입력되지 않았어요.")
                inputUrl()
            } else {
                web!!.loadUrl(url)
            }
        }
        dialog.show()
    }

    override fun onBackPressed() {
        if (web!!.canGoBack()) web!!.goBack()
        else finish()
    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    fun dip2px(dips: Int) = Math.ceil(dips * this.resources.displayMetrics.density.toDouble()).toInt()

}
package com.darkdev.ki

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this)
        layout.orientation = 1
        val txt = TextView(this)
        txt.text = loadLicense()
        txt.textSize = 17f
        txt.setTextColor(Color.BLACK)
        layout.addView(txt)

        val pad = dip2px(20)
        layout.setPadding(pad, dip2px(30), pad, pad)
        val scroll = ScrollView(this)
        scroll.addView(layout)
        setContentView(scroll)
    }

    private fun loadLicense(): String? {
        return try {
            Utils.readAsset(this, "license/license.txt")
        } catch (e: Exception) {
            toast(e.toString())
            "라이선스 정보 불러오기 실패"
        }
    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    fun dip2px(dips: Int) = Math.ceil(dips * this.resources.displayMetrics.density.toDouble()).toInt()

}
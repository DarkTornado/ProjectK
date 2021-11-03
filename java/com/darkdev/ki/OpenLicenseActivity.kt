package com.darkdev.ki

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.darktornado.library.LicenseView

class OpenLicenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this)
        layout.orientation = 1

        layout.addView(LicenseView(this).setTitle("jsoup").setSubtitle("by Jonathan Hedley, MIT License").setLicense("MIT License", "license/Jsoup.txt"))
        layout.addView(LicenseView(this).setTitle("luaj").setSubtitle("by LuaJy, MIT License").setLicense("MIT License", "license/Jsoup.txt"))
        layout.addView(LicenseView(this).setTitle("SimpleRequester").setSubtitle("by Dark Tornado, BSD 3-Clause License").setLicense("BSD 3-Clause License", "license/SimpleRequester.txt"))
        layout.addView(LicenseView(this).setTitle("LicenseView").setSubtitle("by Dark Tornado, BSD 3-Clause License").setLicense("BSD 3-Clause License", "license/LicenseView.txt"))
        layout.addView(LicenseView(this).setTitle("Material Design").setSubtitle("by Google, Apache License 2.0").setLicense("Apache License 2.0", "license/Apache License 2.0.txt"))

        val scroll = ScrollView(this)
        scroll.addView(layout)
        setContentView(scroll)
    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    fun dip2px(dips: Int) = Math.ceil(dips * this.resources.displayMetrics.density.toDouble()).toInt()

}
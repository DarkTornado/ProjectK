package com.darkdev.ki

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SubwayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    fun dip2px(dips: Int) = Math.ceil(dips * this.resources.displayMetrics.density.toDouble()).toInt()

}

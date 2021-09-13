package com.darkdev.ai

import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.darkdev.uilib.CodeEditor
import java.lang.Exception

class ScriptActivity : AppCompatActivity() {

    var editor: CodeEditor? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {

            val layout = LinearLayout(this)
            layout.orientation = 1

            editor = CodeEditor(this)
            editor!!.setHint("Input Source...")
            editor!!.setHintTextColor(Color.GRAY)
            editor!!.setTextColor(Color.BLACK)
            layout.addView(editor)

            layout.setPadding(dip2px(8), 0, 0, dip2px(5))
            val scroll = ScrollView(this)
            scroll.addView(layout)

            setContentView(scroll)
        }catch (e: Exception){
            toast(e.toString())
        }
    }

    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    fun dip2px(dips: Int) = Math.ceil(dips * this.resources.displayMetrics.density.toDouble()).toInt()

}
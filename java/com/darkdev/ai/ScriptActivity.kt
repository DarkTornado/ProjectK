package com.darkdev.ai

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.darkdev.ki.Ki
import com.darkdev.ki.MainService
import com.darkdev.ki.R
import com.darkdev.uilib.CodeEditor

class ScriptActivity : AppCompatActivity() {

    var editor: CodeEditor? = null;

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(0, 0, 0, "저장").setIcon(R.drawable.save).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        menu.add(0, 1, 0, "리로드").setIcon(R.drawable.reload).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            0 -> {
                Ki.saveData(this, "custom_ai", editor!!.text.toString());
                toast("저장되었어요.")
            }
            1 -> {
                val src = Ki.readData(this, "custom_ai")
                val result = MainService.ai.reload(src)
                if (result == null) toast("수정한 내역이 반영되었어요.")
                else toast("리로드 실패\n$result")
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        Thread {
            val src = Ki.readData(this, "custom_ai")
            if (src != null) editor!!.setText(src)
        }.start()
    }


    fun toast(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    fun dip2px(dips: Int) = Math.ceil(dips * this.resources.displayMetrics.density.toDouble()).toInt()

}
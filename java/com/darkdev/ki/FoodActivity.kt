package com.darkdev.ki

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.darkdev.uilib.Item
import com.darkdev.uilib.ListAdapter
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements


class FoodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val input = intent.getStringExtra("input")
        supportActionBar?.title = "맛집 정보 : $input"
        val layout = LinearLayout(this)
        layout.orientation = 1
        StrictMode.enableDefaults()
        val data = loadList(input!!)
        val list = ListView(this)
        val adapter = ListAdapter()
        adapter.setIconSize(0)
        adapter.setItems(data)
        list.setAdapter(adapter)
        list.setOnItemClickListener({ parent, view, pos, id -> showInfo(data[pos]!!) })
        layout.addView(list)

        val pad: Int = dip2px(20)
        list.setPadding(pad, pad, pad, pad)

        setContentView(layout)
    }

    private fun loadList(input: String): Array<Place?> {
        StrictMode.enableDefaults()
        try {
            val noImage = BitmapFactory.decodeStream(assets.open("images/no_image.png"))
            val data: Elements = Jsoup.connect("https://m.map.kakao.com/actions/searchView?q=" + input.replace(" ", "%20") + "%20맛집").get()
                    .select("li.search_item.base")
            val result: Array<Place?> = arrayOfNulls<Place>(data.size)
            for (n in 0 until data.size) {
                val datum: Element = data.get(n)
                val name: String = datum.attr("data-title")
                val addr: String = datum.select("span.txt_g").text()
                val imageUrl: String = datum.select("img").attr("src")
                val image = if (imageUrl == "") noImage else Utils.getImageFromWeb("https://$imageUrl")
                val phone: String = datum.select("a.num_phone").attr("href")
                val url: String = datum.attr("data-id")
                result[n] = Place(name, addr, image, phone, url)
            }
            return result
        } catch (e: Exception) {
            toast(e.toString())
        }
        return arrayOfNulls<Place>(0)
    }

    private fun showInfo(data: Place) {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this)
        dialog.setTitle(data.title)
        dialog.setMessage("""
            주소 : ${data.subtitle}
            전화번호 : ${data.phone.replace("tel:", "")}
            """.trimIndent())
        dialog.setNegativeButton("닫기", null)
        dialog.setPositiveButton("카카오맵") { dialog1, which ->
            val intent = Intent(this@FoodActivity, WebActivity::class.java)
            intent.data = Uri.parse("https://place.map.kakao.com/m/" + data.url)
            startActivity(intent)
        }
        dialog.show()
    }


    fun toast(msg: String) = runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }

    fun dip2px(dips: Int) = Math.ceil(dips * this.resources.displayMetrics.density.toDouble()).toInt()


    private class Place(name: String?, addr: String?, icon: Bitmap?, val phone: String, val url: String) : Item(name, addr, BitmapDrawable(icon))

}
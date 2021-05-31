package com.darkdev.ki

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import org.jsoup.Jsoup
import java.lang.Exception

class BusActivity : AppCompatActivity() {

    var web: WebView? = null
    var busStopList: Array<String?>? = null
    var tick: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bus = intent.getStringExtra("bus")
        val busId = intent.getStringExtra("busId")!!

        supportActionBar?.title = "버스 : $bus"
        web = WebView(this)
        web?.loadUrl("file:///android_asset/bus.html")
        web?.webChromeClient = WebChromeClient()
        web?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                startBusInfo(busId)
                super.onPageFinished(view, url)
            }
        }
        web?.settings?.javaScriptEnabled = true
        setContentView(web)
    }

    fun startBusInfo(busId: String) {
        tick = Thread {
            try {
                loadBusStopList(busId);
                while (true) {
                    updateBusPosition(busId);
                    toast("업데이트됨")
                    Thread.sleep(10000)
                }
            } catch (e: InterruptedException) {
                if (Ki.devModeEnabled) toast("버스 위치 자동 업데이트 중지됨")
            } catch (e: Exception) {
                if (Ki.devModeEnabled) toast("버스 위치 불러오기 실패\n" + e.toString())
                else toast("버스 위치를 불러오지 못했어요.")
            }
        }
        tick?.start()
    }

    override fun onDestroy() {
        if (tick != null) {
            tick?.interrupt()
        }
        super.onDestroy()
    }

    fun loadBusStopList(busId: String) {
        val data = Jsoup.connect("https://m.map.kakao.com/actions/busDetailInfo?busId=$busId").ignoreContentType(true).get()
        val list = data.select("ul.list_route").select("li")
        busStopList = arrayOfNulls(list.size)
        for (n in 0 until list.size) {
            var name = list.get(n).select("strong.tit_route").text()
            val stopId = list.get(n).select("span.txt_route").get(0).ownText().trim()
            if (stopId == "미정차") name = "<font color=#9E9E9E>$name <small>(미정차)</small></font>"
            else name = "$name <font color=#9E9E9E><small>($stopId)</small></font>"
            busStopList!![n] = name
        }
    }

    fun updateBusPosition(busId: String) {
        val data0 = Jsoup.connect("https://m.map.kakao.com/actions/busDetailInfoJson?busId=$busId").ignoreContentType(true).get().wholeText()
        val data = JSONObject(data0)["busLocationList"] as JSONArray
        val count = data.length()
        val busList = arrayOfNulls<Int>(count)
        for (n in 0 until count) {
            val datum = data[n] as JSONObject
            val pos = datum["sectionOrder"] as String
            busList[n] = pos.toInt() - 2
        }

        val icons = "↓◆".toCharArray()
        val result = StringBuilder()
        for (n in 0 until busStopList!!.size) {
            var index = 0
            for (bus in busList) {
                if (bus == n) {
                    index = 1
                    break
                }
            }
            result.append("<tr><td align=center>" + icons[index] + "</td>")
            result.append("<td>" + busStopList!![n] + "</td></tr>")
        }

        runOnUiThread {
            web?.loadUrl("javascript:applyBusInfo('$result')")
        }
    }

    fun toast(msg: String) = runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }

}

package com.darkdev.ai

import android.content.Context
import org.luaj.vm2.Globals
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform

class CustomAI(val ctx: Context) {

    var globals: Globals? = null;

    fun reload(src: String): String? {
        return try {
            globals = JsePlatform.standardGlobals()
            globals!!.set("print", CoerceJavaToLua.coerce(LuaApi.Print()))
            globals!!.set("say", CoerceJavaToLua.coerce(LuaApi.Say()))
            globals!!.set("run_app", CoerceJavaToLua.coerce(LuaApi.RunApp()))
            globals!!.set("open_url", CoerceJavaToLua.coerce(LuaApi.OpenUrl()))
            globals!!.set("get_web_content", CoerceJavaToLua.coerce(LuaApi.GetWebContent()))
            globals!!.set("send_kakao_talk", CoerceJavaToLua.coerce(LuaApi.SendKakaoTalk()))
            val chunk: LuaValue = globals!!.load(src)
            chunk.call()
            null
        } catch (e: Exception) {
            e.toString()
        }
    }

    fun callResponse(msg: String, called: Boolean) {
        val func = globals!!["chatHook"]
        func.call(LuaValue.valueOf(msg), LuaValue.valueOf(called))
    }

}
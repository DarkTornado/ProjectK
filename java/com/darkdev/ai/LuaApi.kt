package com.darkdev.ai

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.speech.tts.TextToSpeech
import com.darkdev.ki.MainService
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction

class LuaApi {

    internal class Say : OneArgFunction() {
        override fun call(msg: LuaValue): LuaValue {
//            toast("[Ki] $msg")
            MainService.tts.speak(msg.tojstring(), TextToSpeech.QUEUE_FLUSH, null)
            return NIL
        }
    }

    internal class RunApp : OneArgFunction() {
        override fun call(packageName: LuaValue): LuaValue {
            val pm: PackageManager = MainService.ctx.getPackageManager()
            MainService.ctx.startActivity(pm.getLaunchIntentForPackage(packageName.tojstring()))
            return NIL
        }
    }

    internal class OpenUrl : OneArgFunction() {
        override fun call(url: LuaValue): LuaValue {
            val uri = Uri.parse(url.tojstring())
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            MainService.ctx.startActivity(intent)
            return NIL
        }
    }
}
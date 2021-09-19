package com.darkdev.ai

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
}
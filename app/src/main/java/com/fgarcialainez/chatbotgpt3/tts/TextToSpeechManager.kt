package com.fgarcialainez.chatbotgpt3.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.preference.PreferenceManager
import com.fgarcialainez.chatbotgpt3.Constants
import com.fgarcialainez.chatbotgpt3.MainApplication
import java.util.*

object TextToSpeechManager: TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    fun init(context: Context) {
        // Initialize TextToSpeech engine
        this.tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set US English as language for tts
            tts!!.setLanguage(Locale.US)
        }
    }

    fun destroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
    }

    fun isEnabled(): Boolean {
        // Get TTS activation settings
        val context = com.fgarcialainez.chatbotgpt3.MainApplication.applicationContext()
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        return preferences.getBoolean(com.fgarcialainez.chatbotgpt3.Constants.TextToSpeechKey, false)
    }

    fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }
}
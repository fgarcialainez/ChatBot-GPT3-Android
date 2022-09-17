package com.fgarcialainez.chatbotgpt3

import android.app.Application
import android.content.Context

class MainApplication : Application() {

    init {
        com.fgarcialainez.chatbotgpt3.MainApplication.Companion.instance = this
    }

    companion object {
        private var instance: com.fgarcialainez.chatbotgpt3.MainApplication? = null

        fun applicationContext() : Context {
            return com.fgarcialainez.chatbotgpt3.MainApplication.Companion.instance!!.applicationContext
        }
    }
}
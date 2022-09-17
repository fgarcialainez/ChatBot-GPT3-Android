package com.fgarcialainez.chatbotgpt3.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.fgarcialainez.chatbotgpt3.R
import com.fgarcialainez.chatbotgpt3.tts.TextToSpeechManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_chat_ai,
                R.id.navigation_factual_answering,
                R.id.navigation_friend_chat,
                R.id.navigation_sarcastic_chat,
                R.id.navigation_smart_chat
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Set app label in the ActionBar
        navController.addOnDestinationChangedListener { _, _, _ ->
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle("ChatBot GPT-3")
        }

        // Initialize TTS
        TextToSpeechManager.init(this)

        // App Center SDK initialization
        AppCenter.start(
            application, com.fgarcialainez.chatbotgpt3.Constants.AppCenterSecret,
            Analytics::class.java, Crashes::class.java
        )
    }

    override fun onDestroy() {
        // Shutdown TTS
        TextToSpeechManager.destroy()

        // Call superclass
        super.onDestroy()
    }
}
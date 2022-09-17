package com.fgarcialainez.chatbotgpt3.models

import androidx.annotation.DrawableRes
import com.fgarcialainez.chatbotgpt3.R

data class Message(
    var user: User,
    var content: String
)

data class User(
    var name: String,
    var isCurrentUser: Boolean,
    @DrawableRes var avatar: Int
)

class MockMessages {
    companion object {
        val botUser = User("Bot", false, R.drawable.chatbot_avatar)
        val felixUser = User("Felix", true, R.drawable.felix_avatar)

        val messages = listOf(
            Message(felixUser, "Hi Bot, how are you?"),
            Message(botUser, "Very well, thanks! What about you?"),
            Message(felixUser, "Very well too! Glad to talk to you here."),
            Message(botUser, "Likewise :)")
        )
    }
}
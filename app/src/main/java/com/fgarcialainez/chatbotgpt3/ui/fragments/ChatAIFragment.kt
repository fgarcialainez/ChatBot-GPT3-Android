package com.fgarcialainez.chatbotgpt3.ui.fragments

import com.fgarcialainez.chatbotgpt3.Constants.SourceType
import com.fgarcialainez.chatbotgpt3.models.ChatAIViewModel
import com.fgarcialainez.chatbotgpt3.models.MessagesListViewModel

class ChatAIFragment : BaseFragment(SourceType.ChatAI) {
    @Suppress("UNCHECKED_CAST")
    override fun <T: com.fgarcialainez.chatbotgpt3.models.MessagesListViewModel> getViewModelClass(): Class<T> {
        return com.fgarcialainez.chatbotgpt3.models.ChatAIViewModel::class.java as Class<T>
    }
}
package com.fgarcialainez.chatbotgpt3.ui.fragments

import com.fgarcialainez.chatbotgpt3.Constants.SourceType
import com.fgarcialainez.chatbotgpt3.models.SmartChatViewModel

class SmartChatFragment : BaseFragment(SourceType.SmartChat) {
    @Suppress("UNCHECKED_CAST")
    override fun <T: com.fgarcialainez.chatbotgpt3.models.MessagesListViewModel> getViewModelClass(): Class<T> {
        return SmartChatViewModel::class.java as Class<T>
    }
}
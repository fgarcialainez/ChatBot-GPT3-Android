package com.fgarcialainez.chatbotgpt3.ui.fragments

import com.fgarcialainez.chatbotgpt3.Constants.SourceType
import com.fgarcialainez.chatbotgpt3.models.MessagesListViewModel
import com.fgarcialainez.chatbotgpt3.models.SarcasticChatViewModel

class SarcasticChatFragment : BaseFragment(SourceType.SarcasticChat) {
    @Suppress("UNCHECKED_CAST")
    override fun <T: com.fgarcialainez.chatbotgpt3.models.MessagesListViewModel> getViewModelClass(): Class<T> {
        return SarcasticChatViewModel::class.java as Class<T>
    }
}
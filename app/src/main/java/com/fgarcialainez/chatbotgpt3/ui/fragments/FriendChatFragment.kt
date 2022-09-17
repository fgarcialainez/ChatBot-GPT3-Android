package com.fgarcialainez.chatbotgpt3.ui.fragments

import com.fgarcialainez.chatbotgpt3.Constants.SourceType
import com.fgarcialainez.chatbotgpt3.models.FriendChatViewModel
import com.fgarcialainez.chatbotgpt3.models.MessagesListViewModel

class FriendChatFragment : BaseFragment(SourceType.FriendChat) {
    @Suppress("UNCHECKED_CAST")
    override fun <T: com.fgarcialainez.chatbotgpt3.models.MessagesListViewModel> getViewModelClass(): Class<T> {
        return com.fgarcialainez.chatbotgpt3.models.FriendChatViewModel::class.java as Class<T>
    }
}
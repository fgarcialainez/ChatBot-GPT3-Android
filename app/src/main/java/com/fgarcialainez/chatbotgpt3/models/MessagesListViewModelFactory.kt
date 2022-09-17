package com.fgarcialainez.chatbotgpt3.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fgarcialainez.chatbotgpt3.Constants.SourceType

class MessagesListViewModelFactory(
    private val sourceType: SourceType,
    private val mockData: Boolean = false
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (sourceType) {
            SourceType.ChatAI -> com.fgarcialainez.chatbotgpt3.models.ChatAIViewModel(mockData) as T
            SourceType.FactualAnswering -> com.fgarcialainez.chatbotgpt3.models.FactualAnsweringViewModel(
                mockData
            ) as T
            SourceType.FriendChat -> com.fgarcialainez.chatbotgpt3.models.FriendChatViewModel(
                mockData
            ) as T
            SourceType.SarcasticChat -> SarcasticChatViewModel(mockData) as T
            SourceType.SmartChat -> SmartChatViewModel(mockData) as T
            else -> throw IllegalArgumentException("Unknown class name")
        }
    }
}
package com.fgarcialainez.chatbotgpt3.ui.fragments

import com.fgarcialainez.chatbotgpt3.Constants.SourceType
import com.fgarcialainez.chatbotgpt3.models.FactualAnsweringViewModel
import com.fgarcialainez.chatbotgpt3.models.MessagesListViewModel

class FactualAnsweringFragment : BaseFragment(SourceType.FactualAnswering) {
    @Suppress("UNCHECKED_CAST")
    override fun <T: com.fgarcialainez.chatbotgpt3.models.MessagesListViewModel> getViewModelClass(): Class<T> {
        return com.fgarcialainez.chatbotgpt3.models.FactualAnsweringViewModel::class.java as Class<T>
    }
}
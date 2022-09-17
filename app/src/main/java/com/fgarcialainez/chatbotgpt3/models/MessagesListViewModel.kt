package com.fgarcialainez.chatbotgpt3.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fgarcialainez.chatbotgpt3.Constants.SourceType
import com.fgarcialainez.chatbotgpt3.services.ServicesManager
import com.fgarcialainez.chatbotgpt3.tts.TextToSpeechManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class MessagesListViewModel(val sourceType: SourceType, mockData: Boolean) : ViewModel() {

    val messagesList = ArrayList<com.fgarcialainez.chatbotgpt3.models.Message>()
    val messagesListLiveData = MutableLiveData<List<com.fgarcialainez.chatbotgpt3.models.Message>>()

    init {
        if (mockData) {
            // Initialize messages list with fake data
            messagesList.addAll(com.fgarcialainez.chatbotgpt3.models.MockMessages.Companion.messages)
            messagesListLiveData.value = messagesList
        }
    }

    fun clearMessages() {
        // Clear messages
        messagesList.clear()

        // Update the live data
        messagesListLiveData.value = messagesList
    }

    fun sendMessage(content: String) {
        // Create the message object
        val message = com.fgarcialainez.chatbotgpt3.models.Message(
            com.fgarcialainez.chatbotgpt3.models.MockMessages.Companion.felixUser,
            content
        )

        // Add the message to the list
        messagesList.add(message)

        // Update the live data
        messagesListLiveData.value = messagesList

        // Generate answer message using OpenAI
        generateAnswerMessage()
    }

    private fun generateAnswerMessage() {
        // Create a new pending message
        val waitingMessage = com.fgarcialainez.chatbotgpt3.models.Message(
            com.fgarcialainez.chatbotgpt3.models.MockMessages.Companion.botUser,
            ""
        )

        // Append the waiting message to the list
        messagesList.add(waitingMessage)

        // Create a new coroutine to move the execution off the UI thread
        viewModelScope.launch(Dispatchers.IO) {
            // Sleep for 2 seconds
            // Thread.sleep(3000)

            // Call GPT-3 API to get the new messages depending on the source type
            val messageContent = ServicesManager.generateAnswerMessage(sourceType, messagesList)

            // Create a new coroutine to move the execution to the main thread
            viewModelScope.launch(Dispatchers.Main) {
                if (messageContent.isNotEmpty()) {
                    // Update the content of the last message
                    messagesList.last().content = messageContent

                    // Perform TTS
                    if (TextToSpeechManager.isEnabled()) {
                        TextToSpeechManager.speakOut(messageContent)
                    }
                }
                else {
                    // If failure, then remove the last message
                    messagesList.removeLast()
                }

                // Update the live data
                messagesListLiveData.value = messagesList
            }
        }
    }
}
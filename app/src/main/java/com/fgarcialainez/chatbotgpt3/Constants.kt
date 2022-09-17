package com.fgarcialainez.chatbotgpt3

object Constants {
    // OpenAI Use Cases Definition
    enum class SourceType {
        ChatAI,
        FactualAnswering,
        QuestionsAnswers,
        FriendChat,
        SarcasticChat,
        SmartChat
    }

    // Preferences Keys
    const val UsernameKey = "USERNAME_KEY"
    const val BiographyKey = "BIOGRAPHY_KEY"
    const val TextToSpeechKey = "TEXT_TO_SPEECH_KEY"

    // External Services
    const val AppCenterSecret = "REPLACE_BY_APP_CENTER_SECRET"
    const val OpenAIApiKey = "REPLACE_BY_OPEN_AI_API_KEY"
}
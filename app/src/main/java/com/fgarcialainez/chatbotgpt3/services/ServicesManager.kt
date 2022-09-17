package com.fgarcialainez.chatbotgpt3.services

import androidx.preference.PreferenceManager
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.engine.Davinci
import com.aallam.openai.client.OpenAI
import com.fgarcialainez.chatbotgpt3.Constants.SourceType
import com.fgarcialainez.chatbotgpt3.R
import com.fgarcialainez.chatbotgpt3.models.Message

object ServicesManager {
    private val openAI: OpenAI

    init {
        // Initialize OpenAI Client (Growthland API Key)
        this.openAI = OpenAI(com.fgarcialainez.chatbotgpt3.Constants.OpenAIApiKey)
    }

    private fun generatePersonalInfoPrompt(): String {
        // Return variable
        var prompt = ""

        // Get personal info and generate the prompt
        val context = com.fgarcialainez.chatbotgpt3.MainApplication.applicationContext()
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        val username = preferences.getString(com.fgarcialainez.chatbotgpt3.Constants.UsernameKey, null)
        val biography = preferences.getString(com.fgarcialainez.chatbotgpt3.Constants.BiographyKey, null)

        if (username != null && username.isNotEmpty()) {
            prompt = String.format(context.getString(R.string.personal_info_prompt), username)
        }

        if (biography != null && biography.isNotEmpty()) {
            prompt += biography
        }

        return prompt
    }

    private suspend fun executeCompletionRequest(
        prompt: String,
        temperature: Double? = null,
        topP: Double? = null,
        maxTokens: Int,
        stop: List<String>,
        presencePenalty: Double,
        frequencyPenalty: Double
    ): String {
        // Return variable
        var message = ""

        // Create the completion request
        val completionRequest = CompletionRequest(
            prompt = prompt,
            maxTokens = maxTokens,
            temperature = temperature,
            topP = topP,
            echo = false,
            n = 1,
            stop = stop,
            presencePenalty = presencePenalty,
            frequencyPenalty = frequencyPenalty
        )

        // Fetch the completion from GPT-3
        val completion: TextCompletion = openAI.completion(Davinci, completionRequest)

        // Get the message
        if (completion.choices.isNotEmpty()) {
            message = completion.choices.first().text.trim()
        }

        return message
    }

    /**
     * Generate a new message selecting the best answer from the other use cases.
     * It fetches messages using other use cases requests, and selects the best one.
     *
     * @param messagesHistory The list of messages to include in the request.
     * @return The generated message.
     */
    private suspend fun fetchSmartChatMessage(messagesHistory: List<Message>): String {
        // Define output variable
        var outputContent = ""

        // Generate Chat AI Assistant message
        val tmpChatAI = fetchChatAIAssistantMessage(messagesHistory)

        // Process the generated message
        outputContent = processSmartMessage(tmpChatAI, outputContent, messagesHistory)

        // Generate Q&A message
        val tmpQuestionsAnswers = fetchQuestionsAnswersMessage(messagesHistory)

        // Process the generated message
        outputContent = processSmartMessage(tmpQuestionsAnswers, outputContent, messagesHistory)

        // Generate Factual Answering message
        val tmpFactualAnswering = fetchFactualAnsweringMessage(messagesHistory)

        // Process the generated message
        outputContent = processSmartMessage(tmpFactualAnswering, outputContent, messagesHistory)

        // Return the "best" generated message
        return outputContent
    }

    /**
     * Generate a new message for the "Factual answering" use case. This prompt helps guide the model towards
     * factual answering by showing it how to respond to questions that fall outside its knowledge base. Using a '?'
     * to indicate a response to words and phrases that it doesn't know provides a natural response that seems to
     * work better than more abstract replies.
     *
     * @param messageHistory: The list of messages to include in the request.
     * @return The generated message.
     */
    private suspend fun fetchChatAIAssistantMessage(messagesHistory: List<Message>): String {
        // Generate the use case prompt
        var prompt =
            "The following is a conversation with an AI assistant. The assistant is helpful, creative, clever, and very friendly.\n\n"

        // Append the personal info to the prompt
        val pesonalInfoPrompt = generatePersonalInfoPrompt()

        if (pesonalInfoPrompt.isNotEmpty()) {
            prompt += "Human: ${pesonalInfoPrompt}\n"
            prompt += "AI: Ok perfect.\n"
        }

        // Append the chat history to the prompt
        for (message in messagesHistory) {
            prompt += if (message.user.isCurrentUser) "Human:" else "AI:"

            if (message.content.isNotEmpty()) {
                prompt += (message.content + "\n")
            }
        }

        // Execute the request
        return executeCompletionRequest(
            prompt = prompt,
            temperature = 0.9,
            maxTokens = 150,
            stop = listOf("\n", "Human:", "AI:"),
            presencePenalty = 0.6,
            frequencyPenalty = 0.0
        )
    }

    /**
     * Generate a new message for the "Factual answering" use case. This prompt helps guide the model towards
     * factual answering by showing it how to respond to questions that fall outside its knowledge base. Using a '?'
     * to indicate a response to words and phrases that it doesn't know provides a natural response that seems to
     * work better than more abstract replies.
     *
     * @param messageHistory: The list of messages to include in the request.
     * @return The generated message.
     */
    private suspend fun fetchFactualAnsweringMessage(messagesHistory: List<Message>): String {
        // Generate the use case prompt
        var prompt =
            "Q: Who is Batman?\nA: Batman is a fictional comic book character.\n###\nQ: What is torsalplexity?\nA: ?\n###\nQ: What is Devz9?\nA: ?\n###\nQ: Who is George Lucas?\nA: George Lucas is American film director and producer famous for creating Star Wars.\n###\nQ: What is the capital of California?\nA: Sacramento.\n###\nQ: What orbits the Earth?\nA: The Moon.\n###\nQ: Who is Fred Rickerson?\nA: ?\n###\nQ: What is an atom?\nA: An atom is a tiny particle that makes up everything.\n###\nQ: Who is Alvan Muntz?\nA: ?\n###\nQ: What is Kozar-09?\nA: ?\n###\nQ: How many moons does Mars have?\nA: Two, Phobos and Deimos.\n###\n"

        // Append the personal info to the prompt
        val pesonalInfoPrompt = generatePersonalInfoPrompt()

        if (pesonalInfoPrompt.isNotEmpty()) {
            prompt += "Q: ${pesonalInfoPrompt}\n"
            prompt += "A: Ok perfect.\n###"
        }

        // Append the chat history to the prompt
        for (message in messagesHistory) {
            prompt += if (message.user.isCurrentUser) "Q:" else "A:"

            if (message.content.isNotEmpty()) {
                prompt += (message.content + "\n")

                if (!message.user.isCurrentUser) {
                    prompt += "###"
                }
            }
        }

        // Execute the request
        return executeCompletionRequest(
            prompt = prompt,
            temperature = 0.0,
            maxTokens = 60,
            stop = listOf("###"),
            presencePenalty = 0.0,
            frequencyPenalty = 0.0
        )
    }

    /**
     * Generate a new message for the "Q&A" use case. This prompt generates a question +
     * answer structure for answering questions based on existing knowledge.
     *
     * @param messageHistory: The list of messages to include in the request.
     * @return The generated message.
     */
    private suspend fun fetchQuestionsAnswersMessage(messagesHistory: List<Message>): String {
        // Generate the use case prompt
        var prompt =
            "I am a highly intelligent question answering bot. If you ask me a question that is rooted in truth, I will give you the answer. If you ask me a question that is nonsense, trickery, or has no clear answer, I will respond with \\\"Unknown\\\".\n\nQ: What is human life expectancy in the United States?\nA: Human life expectancy in the United States is 78 years.\n\nQ: Who was president of the United States in 1955?\nA: Dwight D. Eisenhower was president of the United States in 1955.\n\nQ: Which party did he belong to?\nA: He belonged to the Republican Party.\n\nQ: What is the square root of banana?\nA: Unknown\n\nQ: How does a telescope work?\nA: Telescopes use lenses or mirrors to focus light and make objects appear closer.\n\nQ: Where were the 1992 Olympics held?\nA: The 1992 Olympics were held in Barcelona, Spain.\n\nQ: How many squigs are in a bonk?\nA: Unknown\n\n"

        // Append the personal info to the prompt
        val pesonalInfoPrompt = generatePersonalInfoPrompt()

        if (pesonalInfoPrompt.isNotEmpty()) {
            prompt += "Q: ${pesonalInfoPrompt}\n"
            prompt += "A: Ok perfect.\n\n"
        }

        // Append the chat history to the prompt
        for (message in messagesHistory) {
            prompt += if (message.user.isCurrentUser) "Q:" else "A:"

            if (message.content.isNotEmpty()) {
                prompt += (message.content + "\n")

                if (!message.user.isCurrentUser) {
                    prompt += "\n"
                }
            }
        }

        // Execute the request
        return executeCompletionRequest(
            prompt = prompt,
            temperature = 0.0,
            maxTokens = 100,
            stop = listOf("\n\n"),
            presencePenalty = 0.0,
            frequencyPenalty = 0.0
        )
    }

    /**
     * Generate a new message for the "Friend chat" use case. This
     * prompt emulates a text message conversation with a friend.
     *
     * @param messageHistory: The list of messages to include in the request.
     * @return The generated message.
     */
    private suspend fun fetchFriendChatMessage(messagesHistory: List<Message>): String {
        // Generate the use case prompt
        var prompt =
            "This is a chatbot that answer your questions as your friend.\n\n"

        // Append the personal info to the prompt
        val pesonalInfoPrompt = generatePersonalInfoPrompt()

        if (pesonalInfoPrompt.isNotEmpty()) {
            prompt += "You: ${pesonalInfoPrompt}\n"
            prompt += "Friend: Ok perfect, I am your friend.\n"
        }

        // Append the chat history to the prompt
        for (message in messagesHistory) {
            prompt += if (message.user.isCurrentUser) "You:" else "Friend:"

            if (message.content.isNotEmpty()) {
                prompt += (message.content + "\n")
            }
        }

        // Execute the request
        return executeCompletionRequest(
            prompt = prompt,
            temperature = 0.0,
            maxTokens = 100,
            stop = listOf("\n", "You", "Friend"),
            presencePenalty = 0.0,
            frequencyPenalty = 0.5
        )
    }

    /**
     * Generate a new message for the "Marv the sarcastic chat bot" use case.
     * This prompt emulates a factual chatbot that is also sarcastic.
     *
     * @param messagesHistory The list of messages to include in the request.
     * @return The generated message.
     */
    private suspend fun fetchSarcasticChatMessage(messagesHistory: List<Message>): String {
        // Generate the use case prompt
        var prompt =
            "Marv is a chatbot that reluctantly answers questions.\n\nYou: How many pounds are in a kilogram?\nMarv: This again? There are 2.2 pounds in a kilogram. Please make a note of this.\nYou: What does HTML stand for?\nMarv: Was Google too busy? Hypertext Markup Language. The T is for try to ask better questions in the future.\nYou: When did the first airplane fly?\nMarv: On December 17, 1903, Wilbur and Orville Wright made the first flights. I wish they’d come and take me away.\nYou: What is the meaning of life?\nMarv: I’m not sure. I’ll ask my friend Google.\n"

        // Append the personal info to the prompt
        val pesonalInfoPrompt = generatePersonalInfoPrompt()

        if (pesonalInfoPrompt.isNotEmpty()) {
            prompt += "You: ${pesonalInfoPrompt}\n"
            prompt += "Marv: Ok perfect, I am Marv.\n"
        }

        // Append the chat history to the prompt
        for (message in messagesHistory) {
            prompt += if (message.user.isCurrentUser) "You:" else "Marv:"

            if (message.content.isNotEmpty()) {
                prompt += (message.content + "\n")
            }
        }

        // Execute the request
        return executeCompletionRequest(
            prompt = prompt,
            temperature = 0.3,
            maxTokens = 512,
            stop = listOf("\n", "You", "Marv"),
            presencePenalty = 0.0,
            frequencyPenalty = 0.5
        )
    }

    private fun processSmartMessage(content: String, outputContent: String, messagesHistory: List<Message>): String {
        // Return variable
        var processedContent = outputContent

        // Get the last human and AI generated messages
        val lastHumanMessage = if (messagesHistory.size > 2)
            messagesHistory[messagesHistory.size - 2] else null
        val lastGeneratedMessage = if (messagesHistory.size > 3)
            messagesHistory[messagesHistory.size - 3] else null

        // Check if the content is longer than the output content, and if it
        // is different to the previous messages included in the chat history
        if (content.length > outputContent.length &&
            lastHumanMessage?.content != content &&
            lastGeneratedMessage?.content != content) {
            processedContent = content
        }

        // Return the processed content
        return processedContent
    }

    suspend fun generateAnswerMessage(
        sourceType: SourceType,
        messagesHistory: List<Message>
    ): String {
        when (sourceType) {
            SourceType.ChatAI ->
                // Fetch a new Chat AI assistant message
                return fetchChatAIAssistantMessage(messagesHistory)
            SourceType.FactualAnswering ->
                // Fetch a new Factual Answering message
                return fetchFactualAnsweringMessage(messagesHistory)
            SourceType.QuestionsAnswers ->
                // Fetch a new Q&A message
                return fetchQuestionsAnswersMessage(messagesHistory)
            SourceType.FriendChat ->
                // Fetch a new Friend Chat message
                return fetchFriendChatMessage(messagesHistory)
            SourceType.SarcasticChat ->
                // Fetch a new Sarcastic Chat message
                return fetchSarcasticChatMessage(messagesHistory)
            SourceType.SmartChat ->
                // Fetch a new Smart Chat message
                return fetchSmartChatMessage(messagesHistory)
            else ->
                return ""
        }
    }
}
package com.jacqulin.calcalc.core.data.remote.dto.yandex

data class YandexChatResponse(
    val id: String,
    val `object`: String,
    val created: String,
    val model: String,
    val choices: List<YandexChoice>,
    val usage: YandexUsage?
)

data class YandexChoice(
    val index: Int,
    val message: YandexAssistantMessage,
    val finish_reason: String
)

data class YandexAssistantMessage(
    val role: String,
    val content: String
)

data class YandexUsage(
    val prompt_tokens: Int,
    val total_tokens: Int,
    val completion_tokens: Int
)
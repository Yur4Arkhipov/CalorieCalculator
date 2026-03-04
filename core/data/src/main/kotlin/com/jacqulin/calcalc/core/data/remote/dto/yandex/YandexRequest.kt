package com.jacqulin.calcalc.core.data.remote.dto.yandex

data class YandexChatRequest(
    val model: String,
    val messages: List<YandexMessage>,
    val response_format: YandexResponseFormat? = null,
    val temperature: Double = 0.3,
    val max_output_tokens: Int = 500
)

data class YandexMessage(
    val role: String,
    val content: List<YandexContent>
)

data class YandexContent(
    val type: String,
    val text: String? = null,
    val image_url: YandexImageUrl? = null
)

data class YandexImageUrl(
    val url: String
)

data class YandexResponseFormat(
    val type: String,
    val json_schema: YandexJsonSchema? = null
)

data class YandexJsonSchema(
    val name: String,
    val schema: Map<String, Any>
)
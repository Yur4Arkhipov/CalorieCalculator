package com.jacqulin.calcalc.core.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val model: String = "glm-4.6v-flash",
    val messages: List<MessageRequest>,
    val temperature: Double = 0.1,
    @SerializedName("response_format")
    val responseFormat: ResponseFormat = ResponseFormat()
)

data class MessageRequest(
    val role: String,
    val content: String
)

data class MultimodalMessageRequest(
    val role: String,
    val content: List<ContentPart>
)

data class ContentPart(
    val type: String,
    @SerializedName("image_url")
    val imageUrl: ImageUrl? = null,
    val text: String? = null
)

data class ImageUrl(
    val url: String
)

data class MultimodalChatRequest(
    val model: String = "glm-4.6v-flash",
    val messages: List<MultimodalMessageRequest>,
    val temperature: Double = 0.1,
    @SerializedName("response_format")
    val responseFormat: ResponseFormat = ResponseFormat()
)

data class ResponseFormat(
    val type: String = "json_schema",
    @SerializedName("json_schema")
    val jsonSchema: JsonSchema = JsonSchema()
)

data class JsonSchema(
    val name: String = "nutrition",
    val schema: SchemaObject = SchemaObject()
)

data class SchemaObject(
    val type: String = "object",
    val properties: Map<String, SchemaProperty> = mapOf(
        "calories" to SchemaProperty("number"),
        "protein" to SchemaProperty("number"),
        "fat" to SchemaProperty("number"),
        "carbs" to SchemaProperty("number")
    ),
    val required: List<String> = listOf("calories", "protein", "fat", "carbs"),
    @SerializedName("additionalProperties")
    val additionalProperties: Boolean = false
)

data class SchemaProperty(
    val type: String
)
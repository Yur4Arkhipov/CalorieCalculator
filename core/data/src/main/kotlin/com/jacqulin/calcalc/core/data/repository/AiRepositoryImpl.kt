package com.jacqulin.calcalc.core.data.repository

import com.jacqulin.calcalc.core.data.remote.dto.NutritionDto
import com.jacqulin.calcalc.core.data.remote.dto.NutritionDtoNew
import com.jacqulin.calcalc.core.data.remote.dto.toDomain
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexChatRequest
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexContent
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexImageUrl
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexJsonSchema
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexMessage
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexResponseFormat
import com.jacqulin.calcalc.core.data.remote.service.YandexAiApi
import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.model.NutritionNew
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.util.NotFoodException
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val aiApi: YandexAiApi
) : AiRepository {

    private val model = "gpt://b1gd0h769ghblmknef7n/gemma-3-27b-it/latest"

    private val systemInstructions = """
        Ты профессиональный диетолог.
        Правила:
        - Определи блюдо и разбей его на отдельные продукты/ингредиенты.
        - Для каждого продукта укажи:
          - название
          - примерный вес в граммах
          - калории
          - белки, жиры, углеводы
        - Также укажи суммарные значения для всего блюда.
        - Значения должны быть реалистичными.
        - Если вес неизвестен — оцени его.
        
        Формат ответа строго JSON.
        
        Если это не еда:
        {
          "name": "not_food",
          "items": []
        }
        """

    private val nutritionSchema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "name" to mapOf("type" to "string"),

            "total_calories" to mapOf("type" to "number"),
            "total_protein" to mapOf("type" to "number"),
            "total_fat" to mapOf("type" to "number"),
            "total_carbs" to mapOf("type" to "number"),

            "items" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "name" to mapOf("type" to "string"),
                        "weight" to mapOf("type" to "number"),
                        "calories" to mapOf("type" to "number"),
                        "protein" to mapOf("type" to "number"),
                        "fat" to mapOf("type" to "number"),
                        "carbs" to mapOf("type" to "number")
                    ),
                    "required" to listOf("name","weight","calories","protein","fat","carbs")
                )
            )
        ),
        "required" to listOf("name","items")
    )

    override suspend fun analyzeMeal(description: String): Nutrition {
        val request = buildTextRequest(description)
        val response = aiApi.chat(request)

        val content = response.choices
            .firstOrNull()
            ?.message
            ?.content
            ?: error("AI returned empty response")

        val dto = Json.decodeFromString<NutritionDto>(content)
        if (dto.name?.trim()?.lowercase() == "not_food") throw NotFoodException()
        return dto.toDomain()
    }

    override suspend fun analyzeMealFromImage(imageBase64: String): NutritionNew {
            val request = buildImageRequest(imageBase64)
            val response = aiApi.chat(request)

            val content = response.choices
                .firstOrNull()
                ?.message
                ?.content
                ?: error("AI returned empty response")

            val dto = Json.decodeFromString<NutritionDtoNew>(content)
            if (dto.name?.trim()?.lowercase() == "not_food") throw NotFoodException()
            if (dto.items.isEmpty() && dto.calories == null && dto.total_calories == null) {
                throw Exception("Invalid AI response")
            }
            return dto.toDomain()
    }

    private fun buildTextRequest(description: String) =
        YandexChatRequest(
            model = model,
            messages = listOf(
                YandexMessage(
                    role = "system",
                    content = listOf(
                        YandexContent(
                            type = "text",
                            text = systemInstructions
                        )
                    )
                ),
                YandexMessage(
                    role = "user",
                    content = listOf(
                        YandexContent(
                            type = "text",
                            text = description
                        )
                    )
                )
            ),
            response_format = YandexResponseFormat(
                type = "json_schema",
                json_schema = YandexJsonSchema(
                    name = "nutrition",
                    schema = nutritionSchema
                )
            )
        )

    private fun buildImageRequest(imageBase64: String): YandexChatRequest {
        return YandexChatRequest(
            model = model,
            messages = listOf(
                YandexMessage(
                    role = "system",
                    content = listOf(
                        YandexContent(
                            type = "text",
                            text = systemInstructions
                        )
                    )
                ),
                YandexMessage(
                    role = "user",
                    content = listOf(
                        YandexContent(
                            type = "image_url",
                            image_url = YandexImageUrl(
                                url = "data:image/jpeg;base64,$imageBase64"
                            )
                        )
                    )
                )
            ),
            response_format = YandexResponseFormat(
                type = "json_schema",
                json_schema = YandexJsonSchema(
                    name = "nutrition",
                    schema = nutritionSchema
                )
            )
        )
    }
}
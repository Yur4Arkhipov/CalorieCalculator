package com.jacqulin.calcalc.core.data.repository

import com.jacqulin.calcalc.core.data.remote.dto.NutritionDto
import com.jacqulin.calcalc.core.data.remote.dto.toDomain
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexChatRequest
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexContent
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexImageUrl
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexJsonSchema
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexMessage
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexResponseFormat
import com.jacqulin.calcalc.core.data.remote.service.YandexAiApi
import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val aiApi: YandexAiApi
) : AiRepository {

    private val MODEL = "gpt://b1gd0h769ghblmknef7n/gemma-3-27b-it/latest"

     private val systemInstructions = """
        Ты профессиональный диетолог.
        Правила:
        - Название блюда должно быть конкретным и понятным (например: "Омлет с сыром", а не "Еда").
        - Если на изображении несколько продуктов — объединяй их в одно блюдо.
        - Рассчитывай калорийность и БЖУ для всей порции.
        - Значения должны быть реалистичными и основанными на средних данных.
        - Белки, жиры и углеводы указывай в граммах.
        - Калории указывай в килокалориях.
        - Не добавляй пояснений.
        - Если размер порции неясен, предполагается средняя порция (примерно 250-400 г).
        - Если невозможно точно определить состав — используй наиболее вероятный вариант.
        - Если продукт промышленный (например, шоколадка), ориентируйся на стандартные данные.
        - Не придумывай еду там, где её нет.
        - Если на изображении нет еды, или невозможно определить блюдо, пиши 
        {
          "name": "Not food",
          "calories": 0,
          "protein": 0,
          "fat": 0,
          "carbs": 0
        }
        """.trimIndent()

    private val nutritionSchema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "name" to mapOf("type" to "string"),
            "calories" to mapOf("type" to "number"),
            "protein" to mapOf("type" to "number"),
            "fat" to mapOf("type" to "number"),
            "carbs" to mapOf("type" to "number")
        ),
        "required" to listOf("name","calories","protein","fat","carbs")
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
        return dto.toDomain()
    }

    override suspend fun analyzeMealFromImage(imageBase64: String): Nutrition {
            val request = buildImageRequest(imageBase64)
            val response = aiApi.chat(request)

            val content = response.choices
                .firstOrNull()
                ?.message
                ?.content
                ?: error("AI returned empty response")

            val dto = Json.decodeFromString<NutritionDto>(content)

            return dto.toDomain()
    }

    private fun buildTextRequest(description: String) =
        YandexChatRequest(
            model = MODEL,
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
            model = MODEL,
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
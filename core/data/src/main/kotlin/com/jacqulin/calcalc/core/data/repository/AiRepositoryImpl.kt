package com.jacqulin.calcalc.core.data.repository

import android.util.Log
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
import com.jacqulin.calcalc.core.domain.model.toJsonString
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import com.jacqulin.calcalc.core.util.Result
import com.jacqulin.calcalc.core.util.errors.AppError
import com.jacqulin.calcalc.core.util.errors.ErrorHandler
import kotlinx.serialization.json.Json
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val aiApi: YandexAiApi
) : AiRepository {

    private val model = "gpt://b1gd0h769ghblmknef7n/qwen3.6-35b-a3b/latest"
//    private val model = "gpt://b1gd0h769ghblmknef7n/gemma-3-27b-it/latest"

    private val systemInstructions = """
        Ты профессиональный диетолог и анализатор пищи по изображению.
        
        Твоя задача:
        - определить блюдо на изображении
        - оценить примерный вес порции
        - определить калории и БЖУ
        - определить основные ингредиенты блюда
        
        ВАЖНЫЕ ПРАВИЛА:
        
        - Ответ должен быть ТОЛЬКО валидным JSON.
        - Не используй markdown.
        - Не добавляй пояснения.
        - Не добавляй текст до или после JSON.
        
        - Название блюда должно быть конкретным и понятным.
        - Если на изображении несколько продуктов — объединяй их в одно блюдо.
        - Не придумывай ингредиенты, которых нет на изображении.
        - Если состав определить невозможно — используй наиболее вероятный вариант.
        - Если размер порции неясен — предполагай среднюю порцию 250-400 г.
        - Все значения должны быть реалистичными.
        - Не завышай белки.
        - Не занижай жиры.
        - Используй средние пищевые значения для обычных домашних блюд.
        - Для промышленных продуктов используй типичные данные продукта.
        
        ingredients должен содержать массив объектов.
        
        Каждый ingredient:
        - name — название ингредиента
        - weight — примерный вес ингредиента в граммах
        - calories — калории
        - protein — белки
        - fat — жиры
        - carb — углеводы
        
        Сумма weight всех ingredient должна быть примерно равна общему weight блюда.
        
        Формат ответа:
        
        {
          "name": "string",
          "weight": number,
          "calories": number,
          "protein": number,
          "fat": number,
          "carb": number,
          "ingredient": [
            {
              "name": "string",
              "weight": number,
              "calories": number,
              "protein": number,
              "fat": number,
              "carb": number
            }
          ]
        }
        
        Если на изображении нет еды или блюдо невозможно определить, ответ:
        
        {
          "name": "not_food",
          "weight": 0,
          "calories": 0,
          "protein": 0,
          "fat": 0,
          "carbs": 0,
          "ingredients": []
        }
        """.trimIndent()

    private val nutritionSchema = mapOf(
        "type" to "object",
        "properties" to mapOf(
            "name" to mapOf("type" to "string"),
            "weight" to mapOf("type" to "number"),
            "calories" to mapOf("type" to "number"),
            "protein" to mapOf("type" to "number"),
            "fat" to mapOf("type" to "number"),
            "carb" to mapOf("type" to "number"),
            "ingredient" to mapOf(
                "type" to "array",
                "items" to mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "name" to mapOf(
                            "type" to "string"
                        ),
                        "weight" to mapOf(
                            "type" to "number"
                        ),
                        "calories" to mapOf(
                            "type" to "number"
                        ),
                        "protein" to mapOf(
                            "type" to "number"
                        ),
                        "fat" to mapOf(
                            "type" to "number"
                        ),
                        "carb" to mapOf(
                            "type" to "number"
                        )
                    ),
                    "required" to listOf(
                        "name",
                        "weight",
                        "calories",
                        "protein",
                        "fat",
                        "carb"
                    )
                )
            )
        ),
        "required" to listOf("name", "weight", "calories", "protein", "fat", "carb", "ingredient")
    )

    override suspend fun analyzeMeal(description: String): Result<Nutrition, AppError> {
        return try {
            val request = buildTextRequest(description)
            val response = aiApi.chat(request)

            val content = response.choices
                .firstOrNull()
                ?.message
                ?.content
                ?: error("AI returned empty response")

            val dto = Json.decodeFromString<NutritionDto>(content)
            if (dto.name?.trim()?.lowercase() == "not_food") {
                return Result.Error(AppError.NotFood)
            }
            Result.Success(dto.toDomain())
        } catch (e: Throwable) {
            Result.Error(ErrorHandler.mapError(e))
        }
    }

    override suspend fun analyzeMealFromImage(imageBase64: String): Result <Nutrition, AppError> {
        return try {
            val request = buildImageRequest(imageBase64)
            val response = aiApi.chat(request)

            val content = response.choices
                .firstOrNull()
                ?.message
                ?.content
                ?: error("AI returned empty response")

            val dto = Json.decodeFromString<NutritionDto>(content)
            if (dto.name?.trim()?.lowercase() == "not_food") {
                return Result.Error(AppError.NotFood)
            }
            Result.Success( dto.toDomain())
        } catch (e: Throwable) {
            Log.e("AI_PARSE", "Error", e)
            Result.Error(ErrorHandler.mapError(e))
        }
    }

    override suspend fun refineMeal(
        currentMeal: Nutrition,
        userPrompt: String
    ): Result<Nutrition, AppError> {
        return try {
            val request = buildRefineRequest(
                currentMeal = currentMeal,
                userPrompt = userPrompt
            )
            val response = aiApi.chat(request)
            val content = response.choices
                .firstOrNull()
                ?.message
                ?.content
                ?: error("AI returned empty response")
            val dto = Json.decodeFromString<NutritionDto>(content)
            if (dto.name?.trim()?.lowercase() == "not_food") {
                return Result.Error(AppError.NotFood)
            }
            Result.Success(dto.toDomain())
        } catch (e: Throwable) {
            Result.Error(ErrorHandler.mapError(e))
        }
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

    private fun buildRefineRequest(
        currentMeal: Nutrition,
        userPrompt: String
    ): YandexChatRequest {

        val currentMealJson = currentMeal.toJsonString()

        val refinePrompt = """
            Вот текущий анализ блюда:
            
            $currentMealJson
            
            Пользователь оставил уточнение:
            
            "$userPrompt"
            
            Обнови анализ блюда с учетом уточнения пользователя.
            
            ВАЖНЫЕ ПРАВИЛА:
            
            - Сохраняй структуру JSON.
            - Изменяй только те данные, которые относятся к уточнению.
            - Пересчитай КБЖУ и вес при необходимости.
            - Пересчитай ингредиенты при необходимости.
            - Все значения должны быть реалистичными.
            - Ответ должен содержать полный JSON объекта блюда.
            - Не добавляй пояснений.
            - Ответ должен быть только JSON.
        """.trimIndent()

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
                            type = "text",
                            text = refinePrompt
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
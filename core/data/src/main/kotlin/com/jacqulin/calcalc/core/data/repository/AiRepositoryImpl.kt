package com.jacqulin.calcalc.core.data.repository

import com.google.gson.Gson
import com.jacqulin.calcalc.core.data.remote.dto.ChatRequest
import com.jacqulin.calcalc.core.data.remote.dto.ContentPart
import com.jacqulin.calcalc.core.data.remote.dto.ImageUrl
import com.jacqulin.calcalc.core.data.remote.dto.MessageRequest
import com.jacqulin.calcalc.core.data.remote.dto.MultimodalChatRequest
import com.jacqulin.calcalc.core.data.remote.dto.MultimodalMessageRequest
import com.jacqulin.calcalc.core.data.remote.dto.NutritionDto
import com.jacqulin.calcalc.core.data.remote.dto.toDomain
import com.jacqulin.calcalc.core.data.remote.service.AiApi
import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.repository.AiRepository
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val aiApi: AiApi
) : AiRepository {

    private val gson = Gson()

    override suspend fun analyzeMeal(description: String): Nutrition {
        val request = ChatRequest(
            messages = listOf(
                MessageRequest(
                    role = "system",
                    content = "Ты эксперт по питанию. Анализируй блюда."
                ),
                MessageRequest(
                    role = "user",
                    content = "$description\n\nОпредели точное название блюда и рассчитай его КБЖУ на 100г. Ответь строго в JSON формате: {\"name\": \"название\", \"calories\": число, \"protein\": число, \"fat\": число, \"carbs\": число}"
                )
            )
        )
        val response = aiApi.chatCompletion(request)
        val content = response.choices.first().message.content.trim()
        val nutritionDto = gson.fromJson(content, NutritionDto::class.java)
        return nutritionDto.toDomain()
    }

    override suspend fun analyzeMealFromImage(imageBase64: String): Nutrition {
        val request = MultimodalChatRequest(
            messages = listOf(
                MultimodalMessageRequest(
                    role = "user",
                    content = listOf(
                        ContentPart(
                            type = "image_url",
                            imageUrl = ImageUrl(url = "data:image/jpeg;base64,$imageBase64")
                        ),
                        ContentPart(
                            type = "text",
                            text = "Определи название блюда на фото и рассчитай его КБЖУ (калории, белки, жиры, углеводы) на 100г. Ответь строго в JSON формате: {\"name\": \"название блюда\", \"calories\": число, \"protein\": число, \"fat\": число, \"carbs\": число}"
                        )
                    )
                )
            )
        )
        val response = aiApi.multimodalChatCompletion(request)
        val content = response.choices.first().message.content.trim()
        val nutritionDto = gson.fromJson(content, NutritionDto::class.java)
        return nutritionDto.toDomain()
    }
}
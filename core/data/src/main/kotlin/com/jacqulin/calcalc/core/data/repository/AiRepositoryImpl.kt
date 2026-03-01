package com.jacqulin.calcalc.core.data.repository

import com.google.gson.Gson
import com.jacqulin.calcalc.core.data.remote.dto.ChatRequest
import com.jacqulin.calcalc.core.data.remote.dto.MessageRequest
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
                    content = description
                )
            )
        )
        val response = aiApi.chatCompletion(request)
        val content = response.choices.first().message.content.trim()
        val nutritionDto = gson.fromJson(content, NutritionDto::class.java)
        return nutritionDto.toDomain()
    }
}
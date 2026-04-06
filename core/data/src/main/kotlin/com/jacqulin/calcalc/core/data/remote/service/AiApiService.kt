package com.jacqulin.calcalc.core.data.remote.service

import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexChatRequest
import com.jacqulin.calcalc.core.data.remote.dto.yandex.YandexChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface YandexAiApi {
    @POST("chat/completions")
    suspend fun chat(
        @Body request: YandexChatRequest
    ): YandexChatResponse
}
package com.jacqulin.calcalc.core.data.remote.service

import com.jacqulin.calcalc.core.data.remote.dto.ChatRequest
import com.jacqulin.calcalc.core.data.remote.dto.ChatResponse
import com.jacqulin.calcalc.core.data.remote.dto.MultimodalChatRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AiApi {

    @POST("/v1/chat/completions")
    suspend fun chatCompletion(
        @Body request: ChatRequest
    ): ChatResponse

    @POST("/v1/chat/completions")
    suspend fun multimodalChatCompletion(
        @Body request: MultimodalChatRequest
    ): ChatResponse
}
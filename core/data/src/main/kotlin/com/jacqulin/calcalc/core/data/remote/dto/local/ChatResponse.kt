package com.jacqulin.calcalc.core.data.remote.dto.local

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val choices: List<Choice>
)
package com.jacqulin.calcalc.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val choices: List<Choice>
)
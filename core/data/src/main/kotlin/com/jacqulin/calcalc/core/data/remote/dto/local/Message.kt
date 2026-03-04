package com.jacqulin.calcalc.core.data.remote.dto.local

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val content: String
)
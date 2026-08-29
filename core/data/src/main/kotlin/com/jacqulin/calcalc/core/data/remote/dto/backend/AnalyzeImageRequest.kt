package com.jacqulin.calcalc.core.data.remote.dto.backend

import kotlinx.serialization.Serializable

@Serializable
data class AnalyzeImageRequest(
    val imgBase64: String
)
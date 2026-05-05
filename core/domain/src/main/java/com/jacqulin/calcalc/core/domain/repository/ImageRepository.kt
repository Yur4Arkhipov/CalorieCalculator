package com.jacqulin.calcalc.core.domain.repository

import com.jacqulin.calcalc.core.domain.model.TempImage

interface ImageRepository {
    suspend fun saveImage(imageBytes: ByteArray): String?
    suspend fun encodeForAi(imageBytes: ByteArray): String
    suspend fun deleteImage(path: String)
    suspend fun readImageBytesFromFile(path: String): ByteArray?
    suspend fun createTempImage(): TempImage
    suspend fun deleteTempImage(temp: TempImage)
}
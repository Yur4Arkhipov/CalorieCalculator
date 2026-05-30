package com.jacqulin.calcalc.core.domain.repository

import android.net.Uri
import com.jacqulin.calcalc.core.domain.model.TempImage

interface ImageRepository {
    suspend fun saveImage(imageBytes: ByteArray): String?
    suspend fun encodeForAi(imageBytes: ByteArray): String
    suspend fun deleteImage(path: String)
    suspend fun readImageBytesFromFile(path: String): ByteArray?
    suspend fun createTempImage(): TempImage
    suspend fun copyUriToTemp(uri: Uri): TempImage
    suspend fun deleteTempImage(filePath: String)
}
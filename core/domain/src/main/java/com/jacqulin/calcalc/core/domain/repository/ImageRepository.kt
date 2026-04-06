package com.jacqulin.calcalc.core.domain.repository

import android.net.Uri

interface ImageRepository {
    suspend fun saveImage(imageBytes: ByteArray): String?
    suspend fun encodeForAi(imageBytes: ByteArray): String
    suspend fun deleteImage(path: String)
    suspend fun readImageBytes(uri: Uri): ByteArray?
    suspend fun createCameraFileUri(): Uri
    suspend fun deleteCameraFile(uri: Uri)
}
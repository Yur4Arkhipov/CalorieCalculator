package com.jacqulin.calcalc.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import com.jacqulin.calcalc.core.domain.repository.ImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ImageRepository {

    override suspend fun saveImage(imageBytes: ByteArray): String? =
        withContext(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                val imagesDir = File(context.filesDir, "meal_images").also { it.mkdirs() }
                val file = File(imagesDir, "meal_${System.currentTimeMillis()}.jpg")
                val scaled = scaleBitmap(bitmap, maxDim = 800)
                file.outputStream().use { out ->
                    scaled.compress(Bitmap.CompressFormat.JPEG, 75, out)
                }
                if (scaled !== bitmap) scaled.recycle()
                bitmap.recycle()
                file.absolutePath
            } catch (_: Exception) { null }
        }

    override suspend fun encodeForAi(imageBytes: ByteArray): String =
        withContext(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            val scaled = scaleBitmap(bitmap, maxDim = 1024)
            val output = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 80, output)
            if (scaled !== bitmap) scaled.recycle()
            bitmap.recycle()
            Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
        }

    override suspend fun deleteImage(path: String): Unit =
        withContext(Dispatchers.IO) {
            try { File(path).delete() } catch (_: Exception) {}
        }

    override suspend fun readImageBytes(uri: Uri): ByteArray? =
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            } catch (_: Exception) { null }
        }

    override suspend fun createCameraFileUri(): Uri =
        withContext(Dispatchers.IO) {
            val file = File(context.cacheDir, "meal_photo_${System.currentTimeMillis()}.jpg")
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        }

    override suspend fun deleteCameraFile(uri: Uri): Unit =
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.delete(uri, null, null)
                uri.path?.let { File(it).delete() }
            } catch (_: Exception) {}
        }

    private fun scaleBitmap(bitmap: Bitmap, maxDim: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        if (w <= maxDim && h <= maxDim) return bitmap
        val scale = maxDim.toFloat() / maxOf(w, h)
        return bitmap.scale((w * scale).toInt(), (h * scale).toInt())
    }
}
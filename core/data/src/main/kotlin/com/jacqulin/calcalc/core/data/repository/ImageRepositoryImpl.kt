package com.jacqulin.calcalc.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import com.jacqulin.calcalc.core.domain.model.TempImage
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

            // Получаем размеры
            val bounds = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, bounds)

            val maxDim = 1024
            var sampleSize = 1

            while (maxOf(bounds.outWidth, bounds.outHeight) / sampleSize > maxDim) {
                sampleSize *= 2
            }

            // Декодируем с уменьшением
            val options = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
            }

            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
                ?: throw IllegalStateException("Bitmap decode failed")

            // Дополнительный scale
            val scaled = scaleBitmap(bitmap, maxDim)

            val resultBytes = ByteArrayOutputStream().use { stream ->
                scaled.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                stream.toByteArray()
            }

            if (scaled !== bitmap) scaled.recycle()
            bitmap.recycle()

            Base64.encodeToString(resultBytes, Base64.NO_WRAP)
        }

    override suspend fun deleteImage(path: String): Unit =
        withContext(Dispatchers.IO) {
            try { File(path).delete() } catch (_: Exception) {}
        }


    override suspend fun readImageBytesFromFile(path: String): ByteArray? =
        withContext(Dispatchers.IO) {
            try {
                File(path).readBytes()
            } catch (_: Exception) { null }
        }

    override suspend fun createTempImage(): TempImage =
        withContext(Dispatchers.IO) {
            val file = File(
                context.cacheDir,
                "meal_photo_${System.currentTimeMillis()}.jpg"
            )
            file.createNewFile()
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
            TempImage(uri, file)
        }

    override suspend fun deleteTempImage(temp: TempImage) {
       withContext(Dispatchers.IO) {
           try {
               temp.file.delete()
           } catch (_: Exception) {}
       }
    }

    private fun scaleBitmap(bitmap: Bitmap, maxDim: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height

        if (w <= maxDim && h <= maxDim) return bitmap

        val scale = maxDim.toFloat() / maxOf(w, h)

        val newW = maxOf(1, (w * scale).toInt())
        val newH = maxOf(1, (h * scale).toInt())

        return bitmap.scale(newW, newH)
    }
}
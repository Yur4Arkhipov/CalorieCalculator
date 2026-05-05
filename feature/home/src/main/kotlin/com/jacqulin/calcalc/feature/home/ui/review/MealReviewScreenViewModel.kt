package com.jacqulin.calcalc.feature.home.ui.review

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.repository.ImageRepository
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealFromImageUseCase
import com.jacqulin.calcalc.core.domain.usecase.SaveManualAddMealDBUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.util.NotFoodException
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@HiltViewModel
class MealReviewScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val analyzeMealFromImageUseCase: AnalyzeMealFromImageUseCase,
    private val saveManualAddMealDBUseCase: SaveManualAddMealDBUseCase,
    private val imageRepository: ImageRepository
): ViewModel() {

    private val filePath: String =
        checkNotNull(savedStateHandle["filePath"])

    private val _uiState = MutableStateFlow(MealReviewUiState(isLoading = true))
    val uiState: StateFlow<MealReviewUiState> = _uiState.asStateFlow()

    init {
        analyzeImage()
    }

    private fun analyzeImage() {
        viewModelScope.launch {
            try {
                Log.d("MealReviewViewModel", "filePath: $filePath")
                val bytes = imageRepository.readImageBytesFromFile(filePath)
                    ?: throw IllegalStateException("Failed to read image bytes")

                val result = analyzeMealFromImageUseCase(bytes)

                val analyzedMeal = Meal(
                    id = -1,
                    name = result.nutrition.name.ifBlank { "Блюдо" },
                    calories = result.nutrition.calories.toInt(),
                    proteins = result.nutrition.protein.toInt(),
                    fats = result.nutrition.fat.toInt(),
                    carbs = result.nutrition.carbs.toInt(),
                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    type = MealType.BREAKFAST,
//                    imageUri = result.savedImagePath,
                    isFavorite = false
                )
                _uiState.update { it.copy(meal = analyzedMeal, isLoading = false) }
            } catch (e: NotFoodException) {
                _uiState.update { it.copy(isLoading = false, isError = "На фото не обнаружена еда") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isError = "Ошибка анализа: ${e.message}") }
            }
        }
    }

    fun updateMeal(update: (Meal) -> Meal) {
        _uiState.update { state -> state.copy(meal = state.meal?.let(update)) }
    }


//    fun saveMeal(onSuccess: () -> Unit) {
//        viewModelScope.launch {
//            _uiState.value.meal?.let {
//                saveManualAddMealDBUseCase(it)
//                imageRepository.clearCachedImageUri(cacheKey) // 🔹 Чистим кэш
//                onSuccess()
//            }
//        }
//    }

//    fun discard(onCancel: () -> Unit) {
//        viewModelScope.launch {
//            _uiState.value.meal?.imageUri?.let { uri ->
//                imageRepository.deleteCameraFile(uri.toUri())
//            }
//            imageRepository.clearCachedImageUri(cacheKey) // 🔹 Чистим кэш
//            onCancel()
//        }
//    }
}
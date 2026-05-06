package com.jacqulin.calcalc.feature.home.ui.review

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
import androidx.lifecycle.SavedStateHandle
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import com.jacqulin.calcalc.core.util.NotFoodException
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class MealReviewScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeSelectedDateUseCase: ObserveSelectedDateUseCase,
    private val analyzeMealFromImageUseCase: AnalyzeMealFromImageUseCase,
    private val saveManualAddMealDBUseCase: SaveManualAddMealDBUseCase,
    private val imageRepository: ImageRepository
): ViewModel() {

    private val filePath: String =
        checkNotNull(savedStateHandle["filePath"])

    private val _uiState = MutableStateFlow(MealReviewUiState(isLoading = true))
    val uiState: StateFlow<MealReviewUiState> = _uiState.asStateFlow()

    private val selectedDate = observeSelectedDateUseCase()
    private var cachedBytes: ByteArray? = null

    init {
//        analyzeImage()
        analyzeImageMock()
    }

    private fun analyzeImage() {
        viewModelScope.launch {
            try {
                val bytes = imageRepository.readImageBytesFromFile(filePath)
                    ?: throw IllegalStateException("Failed to read image bytes")
                cachedBytes = bytes
                val result = analyzeMealFromImageUseCase(bytes)
                val analyzedMeal = Meal(
                    id = -1,
                    name = result.nutrition.name.ifBlank { "Meal" },
                    calories = result.nutrition.calories.toInt(),
                    proteins = result.nutrition.protein.toInt(),
                    fats = result.nutrition.fat.toInt(),
                    carbs = result.nutrition.carbs.toInt(),
                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    type = MealType.BREAKFAST,
                    isFavorite = false
                )
                _uiState.update { it.copy(meal = analyzedMeal, isLoading = false) }
            } catch (_: NotFoodException) {
                _uiState.update { it.copy(isLoading = false, isError = "На фото не обнаружена еда") }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isError = "Ошибка анализа: ${e.message}") }
            }
        }
    }

    private fun analyzeImageMock() {
        viewModelScope.launch {
            val analyzedMeal = Meal(
                id = -1,
                name = "Стейк из говядины с овощами и горчицей",
                calories = 550,
                proteins = 45,
                fats = 30,
                carbs = 20,
                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                type = MealType.BREAKFAST,
                isFavorite = false
            )
            _uiState.update {
                it.copy(
                    meal = analyzedMeal,
                    name = analyzedMeal.name,
                    isLoading = false
                )
            }
        }
    }

//    fun updateMeal(update: (Meal) -> Meal) {
//        _uiState.update { state -> state.copy(meal = state.meal?.let(update)) }
//    }

    fun saveMeal() {
        viewModelScope.launch {
            val state = _uiState.value
            val meal = _uiState.value.meal ?: return@launch
            val bytes = cachedBytes ?: imageRepository.readImageBytesFromFile(filePath)
            val savedPath = imageRepository.saveImage(bytes!!)
                ?: return@launch
            val updatedMeal = meal.copy(
                name = state.name,
                calories = state.calories.toIntOrNull() ?: 0,
                proteins = state.proteins.toIntOrNull() ?: 0,
                fats = state.fats.toIntOrNull() ?: 0,
                carbs = state.carbs.toIntOrNull() ?: 0,
                imageUri = savedPath
            )
            saveManualAddMealDBUseCase(
                date = selectedDate.value,
                meal = updatedMeal
            )
            imageRepository.deleteTempImage(filePath)
        }
    }

    fun onNameChange(newValue: String) {
        if (newValue.length <= 40) {
            _uiState.update { it.copy(name = newValue) }
        }
    }

    fun onCaloriesChange(newValue: String) {
        _uiState.update { it.copy(calories = newValue.filter { it.isDigit() }) }
    }

//    fun discard(onCancel: () -> Unit) {
//        viewModelScope.launch {
//            _uiState.value.meal?.imageUri?.let { uri ->
//                imageRepository.deleteCameraFile(uri.toUri())
//            }
//            imageRepository.clearCachedImageUri(cacheKey)
//            onCancel()
//        }
//    }
}
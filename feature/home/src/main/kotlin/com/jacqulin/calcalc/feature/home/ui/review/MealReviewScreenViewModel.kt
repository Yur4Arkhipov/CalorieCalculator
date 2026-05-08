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
import com.jacqulin.calcalc.core.util.funtions.filterNumericInput
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
        analyzeImage()
//        analyzeImageMock()
    }

    private fun analyzeImage() {
        viewModelScope.launch {
            try {
                val bytes = imageRepository.readImageBytesFromFile(filePath)
                    ?: throw IllegalStateException("Failed to read image bytes")
                cachedBytes = bytes
                val result = analyzeMealFromImageUseCase(bytes)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        name = result.nutrition.name.ifBlank { "Meal" },
                        calories = result.nutrition.calories.toInt().toString(),
                        proteins = result.nutrition.protein.toInt().toString(),
                        fats = result.nutrition.fat.toInt().toString(),
                        carbs = result.nutrition.carbs.toInt().toString(),
                        weight = result.nutrition.weight.toInt().toString()
                    )
                }
            } catch (_: NotFoodException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = "На фото не обнаружена еда"
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = "Ошибка анализа: ${e.message}"
                    )
                }
            }
        }
    }

//    private fun analyzeImageMock() {
//        viewModelScope.launch {
//            val analyzedMeal = Meal(
//                id = -1,
//                name = "Стейк из говядины с овощами и горчицей",
//                calories = 550,
//                proteins = 45,
//                fats = 30,
//                carbs = 20,
//                weight = 300,
//                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
//                type = MealType.BREAKFAST,
//                isFavorite = false
//            )
//            _uiState.update {
//                it.copy(
//                    meal = analyzedMeal,
//                    name = analyzedMeal.name,
//                    isLoading = false
//                )
//            }
//        }
//    }

    fun saveMeal() {
        viewModelScope.launch {

            val bytes = cachedBytes ?: imageRepository.readImageBytesFromFile(filePath)
            val savedPath = imageRepository.saveImage(bytes!!)
                ?: return@launch

            val meal = Meal(
                name = _uiState.value.name,
                calories = uiState.value.calories.toIntOrNull() ?: 0,
                proteins = uiState.value.proteins.toIntOrNull() ?: 0,
                fats = uiState.value.fats.toIntOrNull() ?: 0,
                carbs = uiState.value.carbs.toIntOrNull() ?: 0,
                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                type = MealType.BREAKFAST,
                imageUri = savedPath,
                isFavorite = false
            )

            saveManualAddMealDBUseCase(
                date = selectedDate.value,
                meal = meal
            )

            imageRepository.deleteTempImage(filePath)
        }
    }

    fun onNameChange(newValue: String) {
        if (newValue.length <= 40) {
            _uiState.update { it.copy(name = newValue) }
        }
    }

    fun onWeightChanged(newValue: String) {
        val filtered = filterNumericInput(
            input = newValue,
            maxLength = 4,
            maxValue = 2000
        )
        _uiState.update { it.copy(weight = filtered) }
    }

    fun onCaloriesChanged(newValue: String) {
        val filtered = filterNumericInput(
            input = newValue,
            maxLength = 4,
            maxValue = 2500
        )
        _uiState.update { it.copy(calories = filtered) }
    }

    fun onCarbsChanged(newValue: String) {
        val filtered = filterNumericInput(
            input = newValue,
            maxLength = 3,
            maxValue = 300
        )
        _uiState.update { it.copy(carbs = filtered) }
    }

    fun onProteinsChanged(newValue: String) {
        val filtered = filterNumericInput(
            input = newValue,
            maxLength = 3,
            maxValue = 150
        )
        _uiState.update { it.copy(proteins = filtered) }
    }

    fun onFatsChanged(newValue: String) {
        val filtered = filterNumericInput(
            input = newValue,
            maxLength = 3,
            maxValue = 150
        )
        _uiState.update { it.copy(fats = filtered) }
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
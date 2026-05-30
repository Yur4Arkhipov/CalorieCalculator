package com.jacqulin.calcalc.feature.home.ui.review

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Ingredient
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.domain.model.Nutrition
import com.jacqulin.calcalc.core.domain.repository.ImageRepository
import com.jacqulin.calcalc.core.domain.usecase.AnalyzeMealFromImageUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.RefineMealUseCase
import com.jacqulin.calcalc.core.domain.usecase.SaveManualAddMealDBUseCase
import com.jacqulin.calcalc.core.util.NotFoodException
import com.jacqulin.calcalc.core.util.effects.SnackbarMessageCode
import com.jacqulin.calcalc.core.util.effects.UiEffect
import com.jacqulin.calcalc.core.util.funtions.filterNumericInput
import com.jacqulin.calcalc.feature.home.ui.review.mapper.toDomainIngredients
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MealReviewScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeSelectedDateUseCase: ObserveSelectedDateUseCase,
    private val analyzeMealFromImageUseCase: AnalyzeMealFromImageUseCase,
    private val saveManualAddMealDBUseCase: SaveManualAddMealDBUseCase,
    private val refineMealUseCase: RefineMealUseCase,
    private val imageRepository: ImageRepository
): ViewModel() {

    private val filePath: String =
        checkNotNull(savedStateHandle["filePath"])

    private val _uiState = MutableStateFlow(MealReviewUiState(isLoading = true))
    val uiState: StateFlow<MealReviewUiState> = _uiState.asStateFlow()

    private val selectedDate = observeSelectedDateUseCase()
    private var cachedBytes: ByteArray? = null

    private val _effect = Channel<UiEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

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
                _uiState.update { it ->
                    it.copy(
                        isLoading = false,
                        name = result.nutrition.name.ifBlank { "Meal" },
                        calories = result.nutrition.calories.toString(),
                        proteins = result.nutrition.protein.toString(),
                        fats = result.nutrition.fat.toString(),
                        carbs = result.nutrition.carb.toString(),
                        weight = result.nutrition.weight.toString(),
                        ingredients = result.nutrition.ingredient.map {
                            IngredientUi(
                                name = it.name,
                                weight = it.weight.toString(),
                                calories = it.calories.toString(),
                                protein = it.protein.toString(),
                                fat = it.fat.toString(),
                                carb = it.carb.toString()
                            )
                        }
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
                Log.d("Error", "Error: $e")
            }
        }
    }

    private fun analyzeImageMock() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    name = "Стейк из говядины с овощами и горчицей",
                    calories = "450",
                    proteins = "30",
                    fats = "25",
                    carbs = "20",
                    weight = "300",
                    ingredients = listOf(
                        IngredientUi(
                            name = "Говядина",
                            weight = "180",
                            calories = "320",
                            protein = "26",
                            fat = "22",
                            carb = "0"
                        ),
                        IngredientUi(
                            name = "Картофель",
                            weight = "80",
                            calories = "70",
                            protein = "2",
                            fat = "0",
                            carb = "15"
                        ),
                        IngredientUi(
                            name = "Помидоры",
                            weight = "40",
                            calories = "10",
                            protein = "1",
                            fat = "0",
                            carb = "2"
                        ),
                        IngredientUi(
                            name = "Горчица",
                            weight = "10",
                            calories = "50",
                            protein = "1",
                            fat = "3",
                            carb = "3"
                        )
                    )
                )
            }
        }
    }

    fun saveMeal() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                val bytes = cachedBytes ?: imageRepository.readImageBytesFromFile(filePath)
                val savedPath = bytes?.let {
                    imageRepository.saveImage(it)
                } ?: run {
                    _uiState.update { it.copy(isError = "Не удалось сохранить изображение") }
                    _effect.send(
                        UiEffect.ShowSnackbar(
                            messageCode = SnackbarMessageCode.MEAL_SAVE_ERROR,
                            isError = true
                        )
                    )
                    return@launch
                }

                val hasInvalidInput =
                    currentState.weight == "" ||
                    currentState.calories == "" ||
                    currentState.proteins == "" ||
                    currentState.fats == "" ||
                    currentState.carbs == ""

                if (hasInvalidInput) {
                    _effect.send(
                        UiEffect.ShowSnackbar(
                            messageCode = SnackbarMessageCode.MEAL_SAVE_ERROR,
                            isError = true
                        )
                    )
                    return@launch
                }

                val meal = Meal(
                    name = currentState.name,
                    calories = currentState.calories.toIntOrNull() ?: 0,
                    proteins = currentState.proteins.toIntOrNull() ?: 0,
                    fats = currentState.fats.toIntOrNull() ?: 0,
                    carbs = currentState.carbs.toIntOrNull() ?: 0,
                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                    type = MealType.BREAKFAST,
                    imageUri = savedPath,
                    isFavorite = false,
                    ingredient = currentState.ingredients.toDomainIngredients()
                )

                saveManualAddMealDBUseCase(
                    date = selectedDate.value,
                    meal = meal
                )

                imageRepository.deleteTempImage(filePath)
                cachedBytes = null

                _effect.send(
                    element = UiEffect.ShowSnackbar(
                        messageCode = SnackbarMessageCode.MEAL_SAVED,
                        isError = false
                    )
                )
            } catch (_: Exception) {
                _effect.send(
                    element = UiEffect.ShowSnackbar(
                        messageCode = SnackbarMessageCode.MEAL_SAVE_ERROR,
                        isError = true
                    )
                )
            }
        }
    }

    fun onNameChange(newValue: String) {
        if (newValue.length <= 50) {
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

    fun onDescriptionChange(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun onAnalyzeDescription() {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isProcessingDescription = true,
                        isLoading = true
                    )
                }

                val currentMeal = uiState.value.toNutrition()

                val refinedMeal = refineMealUseCase(
                    currentMeal = currentMeal,
                    userPrompt = uiState.value.description
                )

                _uiState.update {
                    it.copy(
                        isProcessingDescription = false,
                        isLoading = false,
                        name = refinedMeal.name,
                        calories = refinedMeal.calories.toString(),
                        proteins = refinedMeal.protein.toString(),
                        fats = refinedMeal.fat.toString(),
                        carbs = refinedMeal.carb.toString(),
                        weight = refinedMeal.weight.toString(),
                        ingredients = refinedMeal.ingredient.map { ingredient ->
                            IngredientUi(
                                name = ingredient.name,
                                weight = ingredient.weight.toString(),
                                calories = ingredient.calories.toString(),
                                protein = ingredient.protein.toString(),
                                fat = ingredient.fat.toString(),
                                carb = ingredient.carb.toString()
                            )
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isProcessingDescription = false,
                        isError = e.message
                    )
                }
            }
        }
    }

    private fun MealReviewUiState.toNutrition(): Nutrition {
        return Nutrition(
            name = name,
            weight = weight.toIntOrNull() ?: 0,
            calories = calories.toIntOrNull() ?: 0,
            protein = proteins.toIntOrNull() ?: 0,
            fat = fats.toIntOrNull() ?: 0,
            carb = carbs.toIntOrNull() ?: 0,
            ingredient = ingredients.map {
                Ingredient(
                    name = it.name,
                    weight = it.weight.toIntOrNull() ?: 0,
                    calories = it.calories.toIntOrNull() ?: 0,
                    protein = it.protein.toIntOrNull() ?: 0,
                    fat = it.fat.toIntOrNull() ?: 0,
                    carb = it.carb.toIntOrNull() ?: 0
                )
            }
        )
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
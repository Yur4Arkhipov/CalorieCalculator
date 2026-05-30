package com.jacqulin.calcalc.feature.home.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.Meal
import com.jacqulin.calcalc.core.domain.repository.MealRepository
import com.jacqulin.calcalc.core.domain.usecase.ObserveSelectedDateUseCase
import com.jacqulin.calcalc.core.domain.usecase.SaveManualAddMealDBUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FavoriteMealChooseViewModel @Inject constructor(
//    getFavoriteMealsUseCase: GetFavoriteMealsUseCase,
    private val observeSelectedDateUseCase: ObserveSelectedDateUseCase,
    private val saveManualAddMealDBUseCase: SaveManualAddMealDBUseCase,
    mealRepository: MealRepository
): ViewModel() {

    val favoriteMeals = mealRepository.observeFavoriteMeals().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val selectedDate: Date
        get() = observeSelectedDateUseCase().value

    fun onMealSelected(meal: Meal) {
        viewModelScope.launch {
            val meal = Meal(
                name = meal.name,
                calories = meal.calories,
                proteins = meal.proteins,
                fats = meal.fats,
                carbs = meal.carbs,
                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                type = meal.type,
                imageUri = meal.imageUri,
                isFavorite = meal.isFavorite
            )
            saveManualAddMealDBUseCase(selectedDate, meal)
        }
    }
}
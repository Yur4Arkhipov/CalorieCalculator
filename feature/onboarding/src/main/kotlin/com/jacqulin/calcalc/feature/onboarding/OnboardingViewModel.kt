package com.jacqulin.calcalc.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.ActivityLevel
import com.jacqulin.calcalc.core.domain.model.Gender
import com.jacqulin.calcalc.core.domain.model.Goal
import com.jacqulin.calcalc.core.domain.model.UserProfile
import com.jacqulin.calcalc.core.domain.repository.OnboardingRepository
import com.jacqulin.calcalc.core.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingState(
    val currentPage: Int = 0,
    val totalPages: Int = 9,
    val age: Int = 20,
    val height: Int = 165,
    val weight: Int = 65,
    val gender: Gender = Gender.MALE,
    val goal: Goal = Goal.MAINTAIN,
    val activityLevel: ActivityLevel = ActivityLevel.ACTIVE,
    val calories: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0
)

sealed interface OnboardingEvent {
    data object NextPage : OnboardingEvent
    data object PreviousPage : OnboardingEvent
    data object Complete : OnboardingEvent
    data class UpdateGender(val gender: Gender) : OnboardingEvent
    data class UpdateGoal(val goal: Goal) : OnboardingEvent
    data class UpdateActivityLevel(val level: ActivityLevel) : OnboardingEvent
    data class UpdateAge(val age: Int) : OnboardingEvent
    data class UpdateHeight(val height: Int) : OnboardingEvent
    data class UpdateWeight(val weight: Int) : OnboardingEvent
    data class UpdateCalories(val calories: Int) : OnboardingEvent
    data class UpdateProtein(val protein: Int) : OnboardingEvent
    data class UpdateFat(val fat: Int) : OnboardingEvent
    data class UpdateCarbs(val carbs: Int) : OnboardingEvent
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state.asStateFlow()

    fun onEvent(event: OnboardingEvent) {
        when (event) {
            OnboardingEvent.NextPage -> {
                _state.update { it.copy(currentPage = (it.currentPage + 1).coerceAtMost(it.totalPages - 1)) }
            }
            OnboardingEvent.PreviousPage -> {
                _state.update { it.copy(currentPage = (it.currentPage - 1).coerceAtLeast(0)) }
            }
            OnboardingEvent.Complete -> completeOnboarding()
            is OnboardingEvent.UpdateGender -> _state.update { it.copy(gender = event.gender) }
            is OnboardingEvent.UpdateAge -> _state.update { it.copy(age = event.age) }
            is OnboardingEvent.UpdateHeight -> _state.update { it.copy(height = event.height) }
            is OnboardingEvent.UpdateWeight -> _state.update { it.copy(weight = event.weight) }
            is OnboardingEvent.UpdateActivityLevel -> _state.update { it.copy(activityLevel = event.level) }
            is OnboardingEvent.UpdateGoal -> _state.update { it.copy(goal = event.goal) }
            is OnboardingEvent.UpdateCalories -> _state.update { it.copy(calories = event.calories) }
            is OnboardingEvent.UpdateProtein -> _state.update { it.copy(protein = event.protein) }
            is OnboardingEvent.UpdateFat -> _state.update { it.copy(fat = event.fat) }
            is OnboardingEvent.UpdateCarbs -> _state.update { it.copy(carbs = event.carbs) }
        }
    }

    fun jumpToPage(page: Int) {
        _state.update { it.copy(currentPage = page.coerceIn(0, it.totalPages - 1)) }
    }

    fun calculateAndGoToLoading() {
        val s = state.value

        val bmr = if (s.gender == Gender.MALE) {
            10 * s.weight + 6.25 * s.height - 5 * s.age + 5
        } else {
            10 * s.weight + 6.25 * s.height - 5 * s.age - 161
        }

        val activityMultiplier = when (s.activityLevel) {
            ActivityLevel.SEDENTARY   -> 1.2f
            ActivityLevel.LIGHT       -> 1.375f
            ActivityLevel.MODERATE    -> 1.55f
            ActivityLevel.ACTIVE      -> 1.725f
            ActivityLevel.VERY_ACTIVE -> 1.9f
        }

        val tdee = bmr * activityMultiplier
        val calories = when (s.goal) {
            Goal.LOSE_WEIGHT -> (tdee * 0.85).toInt()
            Goal.MAINTAIN    -> tdee.toInt()
            Goal.GAIN_WEIGHT -> (tdee * 1.15).toInt()
        }

        val protein = (s.weight * 1.8).toInt()
        val fat = (s.weight * 1.0).toInt()
        val proteinCalories = protein * 4
        val fatCalories = fat * 9
        val carbsCalories = calories - proteinCalories - fatCalories
        val carbs = carbsCalories / 4

        _state.update {
            it.copy(
                calories = calories,
                protein  = protein,
                fat      = fat,
                carbs    = carbs,
                currentPage = 7
            )
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.saveUserData(
                UserProfile(
                    age = state.value.age,
                    height = state.value.height,
                    weight = state.value.weight,
                    gender = state.value.gender,
                    goal = state.value.goal,
                    activityLevel = state.value.activityLevel,
                    caloriesGoal = state.value.calories,
                    carbsGoal = state.value.carbs,
                    fatGoal = state.value.fat,
                    proteinGoal = state.value.protein
                )
            )
            onboardingRepository.setOnboardingCompleted()
        }
    }
}
package com.jacqulin.calcalc.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.ActivityLevel
import com.jacqulin.calcalc.core.domain.model.Gender
import com.jacqulin.calcalc.core.domain.model.Goal
import com.jacqulin.calcalc.core.domain.model.UserProfile
import com.jacqulin.calcalc.core.domain.repository.UserPreferencesRepository
import com.jacqulin.calcalc.core.domain.usecase.DismissMacrosHintUseCase
import com.jacqulin.calcalc.core.domain.usecase.ObserveMacrosHintDismissedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile = UserProfile(
        age = 20,
        height = 165,
        weight = 65,
        gender = Gender.MALE,
        goal = Goal.MAINTAIN,
        activityLevel = ActivityLevel.MODERATE,
        caloriesGoal = 2000,
        proteinGoal = 150,
        carbsGoal = 250,
        fatGoal = 70
    ),
    val isParamsSheetOpen: Boolean = false,
    val isMacrosSheetOpen: Boolean = false,
    val isMacrosHintDismissed: Boolean = false
)

sealed interface ProfileEvent {
    data object OpenParamsSheet : ProfileEvent
    data object CloseParamsSheet : ProfileEvent
    data class SaveParams(
        val age: Int, val height: Int, val weight: Int,
        val gender: Gender, val goal: Goal, val activityLevel: ActivityLevel
    ) : ProfileEvent
    data object OpenMacrosSheet : ProfileEvent
    data object CloseMacrosSheet : ProfileEvent
    data class SaveMacros(
        val calories: Int, val protein: Int,
        val carbs: Int, val fat: Int
    ) : ProfileEvent
    data object DismissMacrosHint : ProfileEvent
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    observeMacrosHintDismissedUseCase: ObserveMacrosHintDismissedUseCase,
    private val dismissMacrosHintUseCase: DismissMacrosHintUseCase
) : ViewModel() {

    private val localState = MutableStateFlow(ProfileUiState())

    val uiState: StateFlow<ProfileUiState> = combine(
        userPreferencesRepository.observeUserProfile(),
        localState,
        observeMacrosHintDismissedUseCase()
    ) { profile, local, hintDismissed ->
        local.copy(
            isLoading = false,
            userProfile = profile,
            isMacrosHintDismissed = hintDismissed
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ProfileUiState(isLoading = true)
    )

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.OpenParamsSheet   -> localState.update { it.copy(isParamsSheetOpen = true) }
            ProfileEvent.CloseParamsSheet  -> localState.update { it.copy(isParamsSheetOpen = false) }
            ProfileEvent.OpenMacrosSheet   -> localState.update { it.copy(isMacrosSheetOpen = true) }
            ProfileEvent.CloseMacrosSheet  -> localState.update { it.copy(isMacrosSheetOpen = false) }
            ProfileEvent.DismissMacrosHint -> viewModelScope.launch { dismissMacrosHintUseCase() }
            is ProfileEvent.SaveParams     -> saveParams(event)
            is ProfileEvent.SaveMacros     -> saveMacros(event)
        }
    }


    private fun saveParams(event: ProfileEvent.SaveParams) {
        val bmr = if (event.gender == Gender.MALE) {
            10.0 * event.weight + 6.25 * event.height - 5.0 * event.age + 5
        } else {
            10.0 * event.weight + 6.25 * event.height - 5.0 * event.age - 161
        }
        val activityMultiplier = when (event.activityLevel) {
            ActivityLevel.SEDENTARY   -> 1.2
            ActivityLevel.LIGHT       -> 1.375
            ActivityLevel.MODERATE    -> 1.55
            ActivityLevel.ACTIVE      -> 1.725
            ActivityLevel.VERY_ACTIVE -> 1.9
        }
        val tdee = bmr * activityMultiplier
        val calories = when (event.goal) {
            Goal.LOSE_WEIGHT -> (tdee * 0.85).toInt()
            Goal.MAINTAIN    -> tdee.toInt()
            Goal.GAIN_WEIGHT -> (tdee * 1.15).toInt()
        }
        val protein = (event.weight * 1.8).toInt()
        val fat     = (event.weight * 1.0).toInt()
        val carbs   = ((calories - protein * 4 - fat * 9) / 4).coerceAtLeast(0)

        viewModelScope.launch {
            userPreferencesRepository.saveUserData(
                UserProfile(
                    age = event.age, height = event.height, weight = event.weight,
                    gender = event.gender, goal = event.goal, activityLevel = event.activityLevel,
                    caloriesGoal = calories, proteinGoal = protein,
                    carbsGoal = carbs, fatGoal = fat
                )
            )
            localState.update { it.copy(isParamsSheetOpen = false) }
        }
    }

    private fun saveMacros(event: ProfileEvent.SaveMacros) {
        viewModelScope.launch {
            val current = uiState.value.userProfile
            userPreferencesRepository.saveUserData(
                current.copy(
                    caloriesGoal = event.calories,
                    proteinGoal  = event.protein,
                    carbsGoal    = event.carbs,
                    fatGoal      = event.fat
                )
            )
            localState.update { it.copy(isMacrosSheetOpen = false) }
        }
    }
}
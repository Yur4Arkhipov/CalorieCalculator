package com.jacqulin.calcalc.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.data.onboarding.OnboardingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingState(
    val currentPage: Int = 0,
    val totalPages: Int = 4,
    val age: String = "",
    val height: String = "",
    val weight: String = "",
    val activityLevel: ActivityLevel? = null
)

enum class ActivityLevel {
    SEDENTARY,
    LIGHT,
    MODERATE,
    ACTIVE,
    VERY_ACTIVE
}

sealed interface OnboardingEvent {
    data object NextPage : OnboardingEvent
    data object PreviousPage : OnboardingEvent
    data object Skip : OnboardingEvent
    data object Complete : OnboardingEvent
    data class UpdateAge(val age: String) : OnboardingEvent
    data class UpdateHeight(val height: String) : OnboardingEvent
    data class UpdateWeight(val weight: String) : OnboardingEvent
    data class UpdateActivityLevel(val level: ActivityLevel) : OnboardingEvent
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingManager: OnboardingManager
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
            OnboardingEvent.Skip -> skipOnboarding()
            OnboardingEvent.Complete -> completeOnboarding()
            is OnboardingEvent.UpdateAge -> _state.update { it.copy(age = event.age) }
            is OnboardingEvent.UpdateHeight -> _state.update { it.copy(height = event.height) }
            is OnboardingEvent.UpdateWeight -> _state.update { it.copy(weight = event.weight) }
            is OnboardingEvent.UpdateActivityLevel -> _state.update { it.copy(activityLevel = event.level) }
        }
    }

    private fun skipOnboarding() {
        viewModelScope.launch {
            onboardingManager.setOnboardingCompleted(skipped = true)
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            onboardingManager.saveUserData(
                age = _state.value.age.toIntOrNull(),
                height = _state.value.height.toFloatOrNull(),
                weight = _state.value.weight.toFloatOrNull(),
                activityLevel = _state.value.activityLevel
            )
            onboardingManager.setOnboardingCompleted(skipped = false)
        }
    }
}
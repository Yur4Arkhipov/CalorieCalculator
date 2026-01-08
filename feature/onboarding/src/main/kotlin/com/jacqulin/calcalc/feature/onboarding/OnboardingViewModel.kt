package com.jacqulin.calcalc.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.domain.model.ActivityLevel
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
    val totalPages: Int = 4,
    val age: Int? = 0,
    val height: Float? = 0f,
    val weight: Float? = 0f,
    val activityLevel: ActivityLevel? = ActivityLevel.ACTIVE
)

sealed interface OnboardingEvent {
    data object NextPage : OnboardingEvent
    data object PreviousPage : OnboardingEvent
    data object Skip : OnboardingEvent
    data object Complete : OnboardingEvent
    data class UpdateAge(val age: Int?) : OnboardingEvent
    data class UpdateHeight(val height: Float?) : OnboardingEvent
    data class UpdateWeight(val weight: Float?) : OnboardingEvent
    data class UpdateActivityLevel(val level: ActivityLevel?) : OnboardingEvent
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
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
            onboardingRepository.setOnboardingCompleted(skipped = true)
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.saveUserData(
                UserProfile(
                    age = state.value.age,
                    height = state.value.height,
                    weight = state.value.weight,
                    activityLevel = state.value.activityLevel
                )
            )
            onboardingRepository.setOnboardingCompleted()
        }
    }
}
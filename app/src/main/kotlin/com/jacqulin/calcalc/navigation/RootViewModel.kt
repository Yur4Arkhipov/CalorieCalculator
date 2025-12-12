package com.jacqulin.calcalc.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacqulin.calcalc.core.data.onboarding.OnboardingManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class RootViewModel @Inject constructor(
    onboardingManager: OnboardingManager
) : ViewModel() {

    val uiState: StateFlow<RootUiState> = onboardingManager.isOnboardingCompleted
        .map { completed ->
            if (completed) RootUiState.Main else RootUiState.Onboarding
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = RootUiState.Loading
        )
}

sealed interface RootUiState {
    data object Loading : RootUiState
    data object Onboarding : RootUiState
    data object Main : RootUiState
}
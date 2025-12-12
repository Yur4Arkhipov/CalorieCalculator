package com.jacqulin.calcalc.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jacqulin.calcalc.feature.onboarding.OnboardingScreen
import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute

fun NavGraphBuilder.onboardingNavigation(
) {
    composable<OnboardingRoute> {
        OnboardingScreen()
    }
}
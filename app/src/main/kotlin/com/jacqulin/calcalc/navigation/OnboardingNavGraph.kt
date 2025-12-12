package com.jacqulin.calcalc.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.jacqulin.calcalc.feature.onboarding.navigation.OnboardingRoute
import com.jacqulin.calcalc.feature.onboarding.navigation.onboardingNavigation

@Composable
fun OnboardingNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = OnboardingRoute
    ) {
        onboardingNavigation()
    }
}
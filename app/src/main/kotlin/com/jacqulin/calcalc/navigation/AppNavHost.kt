package com.jacqulin.calcalc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.jacqulin.calcalc.feature.home.navigation.HomeBaseRoute
import com.jacqulin.calcalc.feature.home.navigation.homeSection
import com.jacqulin.calcalc.feature.home.navigation.navigateToAiMealDescription
import com.jacqulin.calcalc.feature.home.navigation.navigateToMacroDetail
import com.jacqulin.calcalc.feature.home.navigation.navigateToManualAddMeal
import com.jacqulin.calcalc.feature.profile.navigation.profileScreen
import com.jacqulin.calcalc.feature.statistics.navigation.statisticsScreen
import com.jacqulin.calcalc.main.AppState

@Composable
fun AppNavHost(
    appState: AppState,
    modifier: Modifier = Modifier
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = HomeBaseRoute,
        modifier = modifier
    ) {
        homeSection(
            onNavigateToMacroDetail = {
                navController.navigateToMacroDetail()
            },
            onNavigateToAiMealDescription = {
                navController.navigateToAiMealDescription()
            },
            onNavigateToManualAddMeal = {
                navController.navigateToManualAddMeal()
            },
            onBackClick = navController::popBackStack
        )

        statisticsScreen(
            onBackClick = navController::popBackStack
        )

        profileScreen(
            onBackClick = navController::popBackStack
        )
    }
}
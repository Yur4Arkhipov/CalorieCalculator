package com.jacqulin.calcalc.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.jacqulin.calcalc.feature.home.navigation.HomeBaseRoute
import com.jacqulin.calcalc.feature.home.navigation.homeSection
import com.jacqulin.calcalc.feature.home.navigation.navigateToMacroDetail
import com.jacqulin.calcalc.feature.profile.navigation.profileSection
import com.jacqulin.calcalc.feature.statistics.navigation.statisticsSection
import com.jacqulin.calcalc.main.AppState

@Composable
fun MainNavHost(
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
            onBack = {
                navController.popBackStack()
            }
        )

        statisticsSection {

        }

        profileSection {

        }
    }
}
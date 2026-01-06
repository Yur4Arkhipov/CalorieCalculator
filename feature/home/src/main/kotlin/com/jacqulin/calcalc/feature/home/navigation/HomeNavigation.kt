package com.jacqulin.calcalc.feature.home.navigation

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.jacqulin.calcalc.feature.home.HomeViewModel
import com.jacqulin.calcalc.feature.home.ui.HomeScreen
import com.jacqulin.calcalc.feature.home.ui.macrodetail.MacroDetailScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object MacroDetailRoute

@Serializable
data object HomeBaseRoute

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(route = HomeRoute, navOptions)

fun NavController.navigateToMacroDetail() = navigate(route = MacroDetailRoute)

fun NavGraphBuilder.homeSection(
    onNavigateToMacroDetail: () -> Unit,
    onBack: () -> Unit
) {
    navigation<HomeBaseRoute>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(
                onNavigateToMacroDetail = onNavigateToMacroDetail
            )
        }

        composable<MacroDetailRoute> {
            val viewModel: HomeViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            MacroDetailScreen(
                uiState = uiState,
                onBackClick = onBack
            )
        }
    }
}
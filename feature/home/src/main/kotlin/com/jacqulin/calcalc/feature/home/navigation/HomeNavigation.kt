package com.jacqulin.calcalc.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.jacqulin.calcalc.feature.home.ui.home.HomeScreen
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
                onNavigateToMacroDetail = onNavigateToMacroDetail,
            )
        }

        composable<MacroDetailRoute> {
            MacroDetailScreen(
                onBackClick = onBack
            )
        }
    }
}
package com.jacqulin.calcalc.feature.statistics.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.jacqulin.calcalc.feature.statistics.StatisticsScreen
import kotlinx.serialization.Serializable

@Serializable
data object StatisticsRoute

@Serializable
data object StatisticsBaseRoute

fun NavController.navigateToStatistics(navOptions: NavOptions) = navigate(route = StatisticsRoute, navOptions)

fun NavGraphBuilder.friendsSection(
    friendDestinations: NavGraphBuilder.() -> Unit
) {
    navigation<StatisticsBaseRoute>(startDestination = StatisticsRoute) {
        composable<StatisticsRoute>() {
            StatisticsScreen()
        }
        friendDestinations()
    }
}
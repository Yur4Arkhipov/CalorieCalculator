package com.jacqulin.calcalc.feature.statistics.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jacqulin.calcalc.feature.statistics.StatisticsScreen
import kotlinx.serialization.Serializable

@Serializable
data object StatisticsRoute

@Serializable
data object StatisticsBaseRoute

fun NavController.navigateToStatistics(navOptions: NavOptions? = null) =
    navigate(StatisticsRoute, navOptions)

fun NavGraphBuilder.statisticsScreen(
    onBackClick: () -> Unit
) {
    composable<StatisticsRoute> {
        StatisticsScreen(onBackClick = onBackClick)
    }
}
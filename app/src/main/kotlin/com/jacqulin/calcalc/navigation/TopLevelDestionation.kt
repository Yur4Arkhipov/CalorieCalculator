package com.jacqulin.calcalc.navigation

import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.icon.AppIcons
import com.jacqulin.calcalc.feature.home.navigation.HomeBaseRoute
import com.jacqulin.calcalc.feature.home.navigation.HomeRoute
import com.jacqulin.calcalc.feature.profile.navigation.ProfileBaseRoute
import com.jacqulin.calcalc.feature.profile.navigation.ProfileRoute
import com.jacqulin.calcalc.feature.statistics.navigation.StatisticsBaseRoute
import com.jacqulin.calcalc.feature.statistics.navigation.StatisticsRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val iconRes: Int,
    val iconTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route
) {
    STATISTICS(
        iconRes = AppIcons.Statistics,
        iconTextId = R.string.statistics_title,
        route = StatisticsRoute::class,
        baseRoute = StatisticsBaseRoute::class
    ),
    HOME(
        iconRes = AppIcons.Home,
        iconTextId = R.string.home_title,
        route = HomeRoute::class,
        baseRoute = HomeBaseRoute::class
    ),
    PROFILE(
        iconRes = AppIcons.Profile,
        iconTextId = R.string.profile_title,
        route = ProfileRoute::class,
        baseRoute = ProfileBaseRoute::class
    )
}
package com.jacqulin.calcalc.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.jacqulin.calcalc.core.designsystem.icon.AppIcons
import com.jacqulin.calcalc.feature.home.navigation.HomeBaseRoute
import com.jacqulin.calcalc.feature.home.navigation.HomeRoute
import com.jacqulin.calcalc.feature.profile.navigation.ProfileRoute
import com.jacqulin.calcalc.feature.statistics.navigation.StatisticsRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
//    @StringRes val iconTextId: Int,
    val iconTextId: String,
//    @StringRes val titleTextId: Int,
    val titleTextId: String,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route
) {
    STATISTICS(
        selectedIcon = AppIcons.StatisticsSelected,
        unselectedIcon = AppIcons.StatisticsUnselected,
        iconTextId = "Statistics",
        titleTextId = "Statistics",
        route = StatisticsRoute::class,
        baseRoute = StatisticsRoute::class
    ),
    HOME(
        selectedIcon = AppIcons.HomeSelected,
        unselectedIcon = AppIcons.HomeUnselected,
        iconTextId = "Home",
        titleTextId = "Home",
        route = HomeRoute::class,
        baseRoute = HomeBaseRoute::class
    ),
    PROFILE(
        selectedIcon = AppIcons.ProfileSelected,
        unselectedIcon = AppIcons.ProfileUnselected,
        iconTextId = "Profile",
        titleTextId = "Profile",
        route = ProfileRoute::class,
        baseRoute = ProfileRoute::class
    )
//    CHALLENGES(
//
//    ),
}
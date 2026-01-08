package com.jacqulin.calcalc.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.component.TopBarAction
import com.jacqulin.calcalc.core.designsystem.icon.AppIcons
import com.jacqulin.calcalc.feature.home.navigation.HomeBaseRoute
import com.jacqulin.calcalc.feature.home.navigation.HomeRoute
import com.jacqulin.calcalc.feature.profile.navigation.ProfileBaseRoute
import com.jacqulin.calcalc.feature.profile.navigation.ProfileRoute
import com.jacqulin.calcalc.feature.statistics.navigation.StatisticsBaseRoute
import com.jacqulin.calcalc.feature.statistics.navigation.StatisticsRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val icon: ImageVector,
    val iconTextId: String,
    val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route
) {
    STATISTICS(
        icon = AppIcons.StatisticsSelected,
        iconTextId = "Statistics",
        titleTextId = R.string.statistics_title,
        route = StatisticsRoute::class,
        baseRoute = StatisticsBaseRoute::class
    ),
    HOME(
        icon = AppIcons.HomeSelected,
        iconTextId = "Home",
        titleTextId = R.string.home_title,
        route = HomeRoute::class,
        baseRoute = HomeBaseRoute::class
    ),
    PROFILE(
        icon = AppIcons.ProfileSelected,
        iconTextId = "Profile",
        titleTextId = R.string.profile_title,
        route = ProfileRoute::class,
        baseRoute = ProfileBaseRoute::class
    )
}

fun mapDestinationToActions(
    current: TopLevelDestination?,
    onNavigateToTop: (TopLevelDestination) -> Unit
): List<TopBarAction> {
    if (current == null) return emptyList()

    return TopLevelDestination.entries
        .filter { it != current }
        .map { dest ->
            TopBarAction(
                icon = dest.icon,
                contentDescription = dest.iconTextId,
                onClick = { onNavigateToTop(dest) }
            )
        }
}
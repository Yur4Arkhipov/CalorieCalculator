package com.jacqulin.calcalc.main

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.component.TopAppBar
import com.jacqulin.calcalc.core.designsystem.component.TopAppBarConfig
import com.jacqulin.calcalc.feature.home.navigation.AiMealDescriptionRoute
import com.jacqulin.calcalc.feature.home.navigation.MacroDetailRoute
import com.jacqulin.calcalc.navigation.AppNavHost
import com.jacqulin.calcalc.navigation.mapDestinationToActions

@Composable
fun App(appState: AppState) {

    val topAppBarConfig = resolveTopAppBarConfig(appState)
    val actions = mapDestinationToActions(
        current = appState.currentTopLevelDestination,
        onNavigateToTop = { appState.navigateToTopLevelDestination(it) }
    )

    Scaffold(
        topBar = {
            if (topAppBarConfig != null) {
                TopAppBar(
                    titleRes = topAppBarConfig.titleRes,
                    navigationIcon = topAppBarConfig.navigationIcon,
                    onNavigationClick = topAppBarConfig.onNavigationClick,
                    actions = actions
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding() + 10.dp,
                start = paddingValues.calculateStartPadding(LocalLayoutDirection.current) + 10.dp,
                end = paddingValues.calculateEndPadding(LocalLayoutDirection.current) + 10.dp
            )
        ) {
            AppNavHost(
                appState = appState,
            )
        }
    }
}

@Composable
fun resolveTopAppBarConfig(appState: AppState): TopAppBarConfig? {
    val current = appState.currentDestination
    val top = appState.currentTopLevelDestination

    return when {
        top != null -> TopAppBarConfig(
            titleRes = top.titleTextId,
            navigationIcon = null,
            onNavigationClick = null
        )

        current?.hasRoute(MacroDetailRoute::class) == true ->
            TopAppBarConfig(
                titleRes = R.string.macro_detail_title,
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { appState.navController.popBackStack() }
            )

        current?.hasRoute(AiMealDescriptionRoute::class) == true ->
            TopAppBarConfig(
                titleRes = R.string.ai_meal_description_title,
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { appState.navController.popBackStack() }
            )

        else -> null
    }
}
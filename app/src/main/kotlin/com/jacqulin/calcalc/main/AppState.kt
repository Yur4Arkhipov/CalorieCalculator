package com.jacqulin.calcalc.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.jacqulin.calcalc.feature.profile.navigation.navigateToProfile
import com.jacqulin.calcalc.feature.statistics.navigation.navigateToStatistics
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberAppState(
//    networkMonitor: NetworkMonitor,
//    userNewsResourceRepository: UserNewsResourceRepository,
//    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
): AppState {
//    NavigationTrackingSideEffect(navController)
    return remember(
        navController,
        coroutineScope,
//        networkMonitor,
//        userNewsResourceRepository,
//        timeZoneMonitor,
    ) {
        AppState(
            navController = navController,
            coroutineScope = coroutineScope,
//            networkMonitor = networkMonitor,
//            userNewsResourceRepository = userNewsResourceRepository,
//            timeZoneMonitor = timeZoneMonitor,
        )
    }
}

class AppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
//    networkMonitor: NetworkMonitor,
) {
//    val isOffline = networkMonitor.isOnline
//        .map(Boolean::not)
//        .stateIn(
//            scope = coroutineScope,
//            started = SharingStarted.WhileSubscribed(5_000),
//            initialValue = false,
//        )

    private fun getNavOptions() = navOptions {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }

    fun navigateToProfile() {
        navController.navigateToProfile(getNavOptions())
    }

    fun navigateToStatistics() {
        navController.navigateToStatistics(getNavOptions())
    }
}
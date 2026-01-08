package com.jacqulin.calcalc.main

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.component.TopAppBar
import com.jacqulin.calcalc.navigation.AppNavHost

@Composable
fun App(
    appState: AppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
) {
//    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    App(
        appState = appState,
        onNavigateToProfile = { appState.navigateToProfile() },
        onNavigateToStatistics = { appState.navigateToStatistics() },
    )
}

@Composable
internal fun App(
    appState: AppState,
    onNavigateToProfile: () -> Unit,
    onNavigateToStatistics: () -> Unit,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                onNavigateToProfile = onNavigateToProfile,
                onNavigateToStatistics = onNavigateToStatistics
            )
        }
    ) { paddingValues ->
        AppNavHost(
            appState = appState,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    PaddingValues(
                        top = paddingValues.calculateTopPadding(),
                        start = 10.dp,
                        end = 10.dp,
                        bottom = 16.dp
                    )
                )
        )
    }
}
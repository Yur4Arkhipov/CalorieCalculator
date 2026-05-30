package com.jacqulin.calcalc.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.component.BottomBarItem
import com.jacqulin.calcalc.core.designsystem.component.FloatingBottomBar
import com.jacqulin.calcalc.navigation.AppNavHost

@Composable
fun App(appState: AppState) {

//    val currentDestination = appState.currentDestination
//    val currentTopLevel = appState.currentTopLevelDestination
    val bottomBarItems = appState.topLevelDestinations.map { destination ->
        BottomBarItem(
            iconRes = destination.iconRes,
            contentDescription = destination.iconTextId,
            selected = destination == appState.currentTopLevelDestination,
            onClick = { appState.navigateToTopLevelDestination(destination) }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()

    ) {
        AppNavHost(appState = appState)

        if (appState.currentTopLevelDestination != null) {
            FloatingBottomBar(
                items = bottomBarItems,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .navigationBarsPadding()
            )
        }
    }
}

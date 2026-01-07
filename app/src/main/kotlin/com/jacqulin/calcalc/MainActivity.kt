package com.jacqulin.calcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jacqulin.calcalc.core.designsystem.theme.CalorieCalculatorTheme
import com.jacqulin.calcalc.main.App
import com.jacqulin.calcalc.main.rememberAppState
import com.jacqulin.calcalc.navigation.RootScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appState = rememberAppState(
//                networkMonitor = networkMonitor,
//                userNewsResourceRepository = userNewsResourceRepository,
//                timeZoneMonitor = timeZoneMonitor,
            )

            CalorieCalculatorTheme(dynamicColor = false) {
                RootScreen()
            }
        }
    }
}
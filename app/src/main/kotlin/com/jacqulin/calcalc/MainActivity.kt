package com.jacqulin.calcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jacqulin.calcalc.core.designsystem.theme.CalorieCalculatorTheme
import com.jacqulin.calcalc.navigation.RootScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            CalorieCalculatorTheme(dynamicColor = false) {
                RootScreen()
            }
        }
    }
}
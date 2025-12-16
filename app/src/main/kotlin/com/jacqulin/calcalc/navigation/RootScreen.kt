package com.jacqulin.calcalc.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jacqulin.calcalc.main.App
import com.jacqulin.calcalc.main.rememberAppState

@Composable
fun RootScreen(viewModel: RootViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        RootUiState.Loading ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        RootUiState.Onboarding -> OnboardingNavGraph()
        RootUiState.Main -> {
            val appState = rememberAppState()
            App(appState = appState)
        }
    }
}
package com.jacqulin.calcalc.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jacqulin.calcalc.feature.profile.ui.EditMacrosSheet
import com.jacqulin.calcalc.feature.profile.ui.EditParamsSheet
import com.jacqulin.calcalc.feature.profile.ui.NutriGoalsCard
import com.jacqulin.calcalc.feature.profile.ui.ProfileStatsCard
import com.jacqulin.calcalc.feature.profile.ui.SettingsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent
    val paramsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val macrosSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 60.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    ProfileStatsCard(
                        profile = uiState.userProfile,
                        onEditClick = { onEvent(ProfileEvent.OpenParamsSheet) }
                    )
                }
                item {
                    NutriGoalsCard(
                        profile = uiState.userProfile,
                        onEditClick = { onEvent(ProfileEvent.OpenMacrosSheet) },
                        isHintDismissed = uiState.isMacrosHintDismissed,
                        onDismissHint = { onEvent(ProfileEvent.DismissMacrosHint) }
                    )
                }
                item { SettingsCard() }
            }
        }
    }

    if (uiState.isParamsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(ProfileEvent.CloseParamsSheet) },
            sheetState = paramsSheetState
        ) {
            EditParamsSheet(
                profile = uiState.userProfile,
                onDismiss = { onEvent(ProfileEvent.CloseParamsSheet) },
                onSave = { age, height, weight, gender, goal, activity ->
                    onEvent(ProfileEvent.SaveParams(age, height, weight, gender, goal, activity))
                }
            )
        }
    }

    if (uiState.isMacrosSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(ProfileEvent.CloseMacrosSheet) },
            sheetState = macrosSheetState
        ) {
            EditMacrosSheet(
                profile = uiState.userProfile,
                onDismiss = { onEvent(ProfileEvent.CloseMacrosSheet) },
                onSave = { calories, protein, carbs, fat ->
                    onEvent(ProfileEvent.SaveMacros(calories, protein, carbs, fat))
                }
            )
        }
    }
}
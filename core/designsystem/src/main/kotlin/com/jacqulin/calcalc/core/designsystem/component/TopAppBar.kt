package com.jacqulin.calcalc.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
//    @StringRes titleRes: Int,
//    navigationIcon: ImageVector,
//    navigationIconContentDescription: String,
//    actionIcon: ImageVector,
//    actionIconContentDescription: String,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToStatistics: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
//        title = { Text(text = stringResource(id = titleRes)) },
        title = { Text("Калории") },
        actions = {
            IconButton(onClick = onNavigateToStatistics) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Статистика",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onNavigateToProfile) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Профиль",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = colors,
        modifier = modifier.testTag("niaTopAppBar"),
    )
}
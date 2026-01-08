package com.jacqulin.calcalc.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource

data class TopAppBarConfig(
    val titleRes: Int,
    val navigationIcon: ImageVector? = null,
    val onNavigationClick: (() -> Unit)? = null
)

data class TopBarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    onNavigationClick: (() -> Unit)? = null,
    actions: List<TopBarAction> = emptyList(),
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors()
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(titleRes)) },
        navigationIcon = {
            if (navigationIcon != null && onNavigationClick != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(navigationIcon, contentDescription = null)
                }
            }
        },
        actions = {
            actions.forEach { action ->
                IconButton(onClick = action.onClick) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.contentDescription
                    )
                }
            }
        },
        colors = colors,
        modifier = modifier.testTag("TopAppBar"),
    )
}
package com.jacqulin.calcalc.core.designsystem.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

data class BottomBarItem(
    val iconRes: Int,
    val contentDescription: Int,
    val selected: Boolean,
    val onClick: () -> Unit
)

@Composable
fun FloatingBottomBar(
    items: List<BottomBarItem>,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .height(48.dp)
            .widthIn(max = 300.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.selected,
                onClick = item.onClick,
                icon = {
                    Icon(
                        painter = painterResource(item.iconRes),
                        contentDescription = stringResource(item.contentDescription)
                    )
                }
            )
        }
    }
}
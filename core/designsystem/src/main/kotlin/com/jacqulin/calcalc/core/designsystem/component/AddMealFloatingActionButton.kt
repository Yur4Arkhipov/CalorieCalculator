package com.jacqulin.calcalc.core.designsystem.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun AddMealFloatingActionButton(
    icon: Painter,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(60.dp),
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
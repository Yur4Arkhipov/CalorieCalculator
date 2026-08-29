package com.jacqulin.calcalc.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.theme.AppOnPrimary
import com.jacqulin.calcalc.core.designsystem.theme.Favorite

@Composable
fun TopOverlay(
    modifier: Modifier = Modifier,
    isError: Boolean,
    isFavorite: Boolean = false,
    showFavoriteButton: Boolean = false,
    onSaveClick: () -> Unit = {},
    onFavoriteClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val buttonModifier = Modifier
        .size(40.dp)
        .background(
            color = AppOnPrimary,
            shape = CircleShape
        )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onBackClick,
            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
            modifier = buttonModifier
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(R.string.detail_back),
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        if (showFavoriteButton) {
            IconButton(
                onClick = onFavoriteClick,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                modifier = buttonModifier

            ) {
                Icon(
                    painter = if (isFavorite)
                        painterResource(R.drawable.ic_favorite_filled)
                    else
                        painterResource(R.drawable.ic_favorite),
                    contentDescription = null,
                    tint = if (isFavorite) Favorite else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else if (!isError) {
            IconButton(
                onClick = onSaveClick,
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
                modifier = buttonModifier
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
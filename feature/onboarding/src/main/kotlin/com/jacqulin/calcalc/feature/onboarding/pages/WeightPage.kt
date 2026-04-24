package com.jacqulin.calcalc.feature.onboarding.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jacqulin.calcalc.core.designsystem.R
import io.github.plovotok.wheelpicker.OverlayConfiguration
import io.github.plovotok.wheelpicker.WheelPicker
import io.github.plovotok.wheelpicker.rememberWheelPickerState

@Composable
fun WeightPage(
    selectedWeight: Int?,
    onWeightSelected: (Int) -> Unit
) {
    val list = remember { (30..200).map { it } }

    val initialIndex = remember(selectedWeight) {
        ((selectedWeight ?: 65) - 30).coerceIn(0, list.lastIndex)
    }

    val pickerState = rememberWheelPickerState(
        initialIndex = initialIndex,
        infinite = false
    )

    val selectedIndex by pickerState.selectedItemState(list.size)

    LaunchedEffect(selectedIndex) {
        onWeightSelected(list[selectedIndex])
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.onboarding_weight_title),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        WheelPicker(
            data = list,
            state = pickerState,
            overlay = OverlayConfiguration.create(
                scrimColor = Color.Transparent,
                focusColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            ),
            itemContent = { index ->
                val isSelected = index == selectedIndex
                Text(
                    text = stringResource(R.string.onboarding_weight_format, list[index]),
                    fontSize = if (isSelected) 24.sp else 18.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        )
    }
}
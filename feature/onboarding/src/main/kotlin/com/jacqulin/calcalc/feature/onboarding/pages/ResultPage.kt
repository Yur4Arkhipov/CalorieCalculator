package com.jacqulin.calcalc.feature.onboarding.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R

@Composable
fun ResultPage(
    calories: Int,
    protein: Int,
    fat: Int,
    carbs: Int,
    onCaloriesChange: (Int) -> Unit,
    onProteinChange: (Int) -> Unit,
    onFatChange: (Int) -> Unit,
    onCarbsChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.onboarding_result_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(R.string.onboarding_result_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 32.dp)
        )

        NutrientField(
            label = stringResource(R.string.calories),
            value = calories,
            unit = stringResource(R.string.calories_suffix),
            onValueChange = onCaloriesChange
        )
        Spacer(Modifier.height(12.dp))
        NutrientField(
            label = stringResource(R.string.proteins),
            value = protein,
            unit = stringResource(R.string.weight_suffix),
            onValueChange = onProteinChange
        )
        Spacer(Modifier.height(12.dp))
        NutrientField(
            label = stringResource(R.string.fats),
            value = fat,
            unit = stringResource(R.string.weight_suffix),
            onValueChange = onFatChange
        )
        Spacer(Modifier.height(12.dp))
        NutrientField(
            label = stringResource(R.string.carbs),
            value = carbs,
            unit = stringResource(R.string.weight_suffix),
            onValueChange = onCarbsChange
        )
    }
}

@Composable
private fun NutrientField(
    label: String,
    value: Int,
    unit: String,
    onValueChange: (Int) -> Unit
) {
    var text by remember(value) { mutableStateOf(value.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = text,
            onValueChange = { input ->
                text = input
                input.toIntOrNull()?.let { onValueChange(it) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            suffix = { Text(unit) },
            singleLine = true,
            modifier = Modifier.width(130.dp)
        )
    }
}
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
import com.jacqulin.calcalc.core.util.funtions.filterNumericInput

@Composable
fun ResultPage(
    calories: String,
    protein: String,
    fat: String,
    carbs: String,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit
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
            maxValue = 8000,
            maxLength = 4,
            onValueChange = onCaloriesChange
        )
        Spacer(Modifier.height(12.dp))
        NutrientField(
            label = stringResource(R.string.proteins),
            value = protein,
            unit = stringResource(R.string.weight_suffix),
            maxValue = 500,
            maxLength = 3,
            onValueChange = onProteinChange
        )
        Spacer(Modifier.height(12.dp))
        NutrientField(
            label = stringResource(R.string.fats),
            value = fat,
            unit = stringResource(R.string.weight_suffix),
            maxValue = 300,
            maxLength = 3,
            onValueChange = onFatChange
        )
        Spacer(Modifier.height(12.dp))
        NutrientField(
            label = stringResource(R.string.carbs),
            value = carbs,
            maxValue = 1000,
            maxLength = 3,
            unit = stringResource(R.string.weight_suffix),
            onValueChange = onCarbsChange
        )
    }
}

@Composable
private fun NutrientField(
    label: String,
    value: String,
    unit: String,
    maxLength: Int,
    maxValue: Int,
    onValueChange: (String) -> Unit
) {
    var text by remember(value) { mutableStateOf(value) }

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
                val filtered = filterNumericInput(
                    input = input,
                    maxLength = maxLength,
                    maxValue = maxValue
                )
                text = filtered
                onValueChange(filtered)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            suffix = { Text(unit) },
            singleLine = true,
            modifier = Modifier.width(130.dp)
        )
    }
}
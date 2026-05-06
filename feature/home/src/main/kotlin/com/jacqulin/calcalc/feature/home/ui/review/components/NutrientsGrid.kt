package com.jacqulin.calcalc.feature.home.ui.review.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R

@Composable
fun NutrientsGrid(
    calories: String,
    protein: String,
    fat: String,
    carbs: String,
    onCaloriesChange: (String) -> Unit,
    onProteinChange: (String) -> Unit,
    onFatChange: (String) -> Unit,
    onCarbsChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        Row(Modifier.fillMaxWidth()) {
            NutrientItem(
                label = stringResource(R.string.calories),
                value = calories,
                onValueChange = onCaloriesChange,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(12.dp))
            NutrientItem(
                label = stringResource(R.string.proteins),
                value = protein,
                onValueChange = onProteinChange,
                modifier = Modifier.weight(1f),
                suffix = stringResource(R.string.weight_suffix)
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(Modifier.fillMaxWidth()) {
            NutrientItem(
                label = stringResource(R.string.fats),
                value = fat,
                onValueChange = onFatChange,
                modifier = Modifier.weight(1f),
                suffix = stringResource(R.string.weight_suffix)
            )
            Spacer(Modifier.width(12.dp))
            NutrientItem(
                label = stringResource(R.string.carbs),
                value = carbs,
                onValueChange = onCarbsChange,
                modifier = Modifier.weight(1f),
                suffix = stringResource(R.string.weight_suffix)
            )
        }
    }
}

@Composable
fun NutrientItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    suffix: String? = null
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            suffix = {
                suffix?.let { Text(it) }
            }
        )
    }
}
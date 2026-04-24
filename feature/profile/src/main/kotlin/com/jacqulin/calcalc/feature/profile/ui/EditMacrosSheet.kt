package com.jacqulin.calcalc.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.domain.model.UserProfile
import com.jacqulin.calcalc.core.util.funtions.filterNumericInput

@Composable
fun EditMacrosSheet(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int, Int) -> Unit
) {
    var calories by remember(profile) { mutableStateOf(profile.caloriesGoal.toString()) }
    var protein by remember(profile) { mutableStateOf(profile.proteinGoal.toString()) }
    var carbs by remember(profile) { mutableStateOf(profile.carbsGoal.toString()) }
    var fats by remember(profile) { mutableStateOf(profile.fatGoal.toString()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            bottom = 30.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.profile_daily_norms),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Text(
                text = stringResource(R.string.profile_edit_macros_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        item {
            Column {
                NutrientEditField(
                    label = stringResource(R.string.calories),
                    value = calories,
                    unit = stringResource(R.string.calories_suffix),
                    maxLength = 4,
                    maxValue = 2500
                ) { calories = it }
                NutrientEditField(
                    label = stringResource(R.string.proteins),
                    value = protein,
                    unit = stringResource(R.string.weight_suffix),
                    maxLength = 3,
                    maxValue = 150
                ) { protein = it }
                NutrientEditField(
                    label = stringResource(R.string.carbs),
                    value = carbs,
                    unit = stringResource(R.string.weight_suffix),
                    maxLength = 3,
                    maxValue = 300
                ) { carbs = it }
                NutrientEditField(
                    label = stringResource(R.string.fats),
                    value = fats,
                    unit = stringResource(R.string.weight_suffix),
                    maxLength = 3,
                    maxValue = 150
                ) { fats = it }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.home_dialog_cancel))
                }
                Button(
                    onClick = {
                        onSave(
                            calories.toIntOrNull() ?: profile.caloriesGoal,
                            protein.toIntOrNull() ?: profile.proteinGoal,
                            carbs.toIntOrNull() ?: profile.carbsGoal,
                            fats.toIntOrNull() ?: profile.fatGoal
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = calories.isNotBlank()
                            && protein.isNotBlank()
                            && carbs.isNotBlank()
                            && fats.isNotBlank()
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@Composable
private fun NutrientEditField(
    label: String,
    value: String,
    unit: String,
    maxLength: Int,
    maxValue: Int,
    onValueChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = value,
            onValueChange = { input ->
                val filtered = filterNumericInput(
                    input = input,
                    maxLength = maxLength,
                    maxValue = maxValue
                )
                onValueChanged(filtered)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            suffix = { Text(unit) },
            singleLine = true,
            modifier = Modifier.width(130.dp),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
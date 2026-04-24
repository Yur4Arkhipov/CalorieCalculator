package com.jacqulin.calcalc.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.domain.model.ActivityLevel
import com.jacqulin.calcalc.core.domain.model.Gender
import com.jacqulin.calcalc.core.domain.model.Goal
import com.jacqulin.calcalc.core.domain.model.UserProfile
import com.jacqulin.calcalc.core.util.funtions.filterNumericInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditParamsSheet(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int, Gender, Goal, ActivityLevel) -> Unit
) {
    var age by remember(profile) { mutableStateOf(profile.age.toString()) }
    var height by remember(profile) { mutableStateOf(profile.height.toString()) }
    var weight by remember(profile) { mutableStateOf(profile.weight.toString()) }
    var gender by remember(profile) { mutableStateOf(profile.gender) }
    var goal by remember(profile) { mutableStateOf(profile.goal) }
    var activityLevel by remember(profile) { mutableStateOf(profile.activityLevel) }
    var expandedGoal by remember { mutableStateOf(false) }
    var expandedActivity by remember { mutableStateOf(false) }

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
                text = stringResource(R.string.profile_my_parameters),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = age,
                    onValueChange = { input ->
                        val filtered = filterNumericInput(
                            input = input,
                            maxLength = 3,
                            maxValue = 150
                        )
                        age = filtered
                    },
                    label = { Text(stringResource(R.string.profile_age)) },
                    suffix = { Text(stringResource(R.string.profile_age_suffix_simple)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { input ->
                        val filtered = filterNumericInput(
                            input = input,
                            maxLength = 3,
                            maxValue = 250
                        )
                        height = filtered
                    },
                    label = { Text(stringResource(R.string.profile_height)) },
                    suffix = { Text(stringResource(R.string.profile_height_suffix_simple)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { input ->
                        val filtered = filterNumericInput(
                            input = input,
                            maxLength = 3,
                            maxValue = 300
                        )
                        weight = filtered
                    },
                    label = { Text(stringResource(R.string.profile_weight)) },
                    suffix = { Text(stringResource(R.string.profile_weight_suffix_simple)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
        item {
            Column {
                SectionLabel(stringResource(R.string.profile_gender))
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Gender.entries.forEach { g ->
                        val isSelected = gender == g
                        FilterChip(
                            selected = isSelected,
                            onClick = { gender = g },
                            label = {
                                Text(
                                    text = if (g == Gender.MALE)
                                        stringResource(R.string.profile_gender_male)
                                    else
                                        stringResource(R.string.profile_gender_female),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            },
                            shape = RoundedCornerShape(50),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                selectedBorderColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
        item {
            Column {
                SectionLabel(stringResource(R.string.profile_goal))
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedGoal,
                    onExpandedChange = { expandedGoal = it }
                ) {
                    OutlinedTextField(
                        value = goalDisplayName(goal),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.profile_goal)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGoal) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedGoal,
                        onDismissRequest = { expandedGoal = false },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        Goal.entries.forEachIndexed { index, g ->
                            DropdownMenuItem(
                                text = { Text(goalDisplayName(g)) },
                                colors = MenuDefaults.itemColors(
                                    textColor = Color.Black,
                                    leadingIconColor = Color.Black,
                                    trailingIconColor = Color.Gray
                                ),
                                onClick = {
                                    goal = g
                                    expandedGoal = false
                                }
                            )
                            if (index != Goal.entries.lastIndex) {
                                HorizontalDivider(
                                    color = Color.Black.copy(alpha = 0.08f)
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Column {
                SectionLabel(stringResource(R.string.profile_activity_level))
                Spacer(Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expandedActivity,
                    onExpandedChange = { expandedActivity = it }
                ) {
                    OutlinedTextField(
                        value = activityDisplayName(activityLevel),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.profile_activity_level)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActivity) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedActivity,
                        onDismissRequest = { expandedActivity = false },
                        shape = RoundedCornerShape(16.dp),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        ActivityLevel.entries.forEachIndexed { index, a ->
                            DropdownMenuItem(
                                text = { Text(activityDisplayName(a)) },
                                colors = MenuDefaults.itemColors(
                                    textColor = Color.Black,
                                    leadingIconColor = Color.Black,
                                    trailingIconColor = Color.Gray
                                ),
                                onClick = {
                                    activityLevel = a
                                    expandedActivity = false
                                }
                            )
                            if (index != ActivityLevel.entries.lastIndex) {
                                HorizontalDivider(
                                    color = Color.Black.copy(alpha = 0.08f)
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(stringResource(R.string.home_dialog_cancel)) }
                Button(
                    onClick = {
                        onSave(
                            age.toIntOrNull() ?: profile.age,
                            height.toIntOrNull() ?: profile.height,
                            weight.toIntOrNull() ?: profile.weight,
                            gender, goal, activityLevel
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = age.isNotBlank() && height.isNotBlank() && weight.isNotBlank()
                ) { Text(stringResource(R.string.save)) }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )
}

@Composable
private fun activityDisplayName(level: ActivityLevel) = when (level) {
    ActivityLevel.SEDENTARY   -> stringResource(R.string.profile_activity_sedentary)
    ActivityLevel.LIGHT       -> stringResource(R.string.profile_activity_light)
    ActivityLevel.MODERATE    -> stringResource(R.string.profile_activity_moderate)
    ActivityLevel.ACTIVE      -> stringResource(R.string.profile_activity_active)
    ActivityLevel.VERY_ACTIVE -> stringResource(R.string.profile_activity_max)
}

@Composable
private fun goalDisplayName(goal: Goal) = when (goal) {
    Goal.LOSE_WEIGHT -> stringResource(R.string.profile_goal_lose)
    Goal.MAINTAIN    -> stringResource(R.string.profile_goal_maintain)
    Goal.GAIN_WEIGHT -> stringResource(R.string.profile_goal_gain)
}
package com.jacqulin.calcalc.feature.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jacqulin.calcalc.core.domain.model.ActivityLevel
import com.jacqulin.calcalc.core.domain.model.Gender
import com.jacqulin.calcalc.core.domain.model.Goal
import com.jacqulin.calcalc.core.domain.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent
    val paramsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val macrosSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 60.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    ProfileStatsCard(
                        profile = uiState.userProfile,
                        onEditClick = { onEvent(ProfileEvent.OpenParamsSheet) }
                    )
                }
                item {
                    NutriGoalsCard(
                        profile = uiState.userProfile,
                        onEditClick = { onEvent(ProfileEvent.OpenMacrosSheet) },
                        isHintDismissed = uiState.isMacrosHintDismissed,
                        onDismissHint = { onEvent(ProfileEvent.DismissMacrosHint) }
                    )
                }
                item { SettingsCard() }
            }
        }
    }

    if (uiState.isParamsSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(ProfileEvent.CloseParamsSheet) },
            sheetState = paramsSheetState
        ) {
            EditParamsSheet(
                profile = uiState.userProfile,
                onDismiss = { onEvent(ProfileEvent.CloseParamsSheet) },
                onSave = { age, height, weight, gender, goal, activity ->
                    onEvent(ProfileEvent.SaveParams(age, height, weight, gender, goal, activity))
                }
            )
        }
    }

    if (uiState.isMacrosSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(ProfileEvent.CloseMacrosSheet) },
            sheetState = macrosSheetState
        ) {
            EditMacrosSheet(
                profile = uiState.userProfile,
                onDismiss = { onEvent(ProfileEvent.CloseMacrosSheet) },
                onSave = { calories, protein, carbs, fat ->
                    onEvent(ProfileEvent.SaveMacros(calories, protein, carbs, fat))
                }
            )
        }
    }
}

@Composable
private fun ProfileStatsCard(profile: UserProfile, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Мои параметры",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Изменить")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatItem(
                    icon = Icons.Default.Person,
                    label = "Возраст",
                    value = "${profile.age} лет",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = Icons.Default.Height,
                    label = "Рост",
                    value = "${profile.height} см",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = Icons.Default.MonitorWeight,
                    label = "Вес",
                    value = "${profile.weight} кг",
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatItem(
                    icon = Icons.Default.Person,
                    label = "Пол",
                    value = if (profile.gender == Gender.MALE) "Мужской" else "Женский",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = Icons.Default.TrackChanges,
                    label = "Цель",
                    value = when (profile.goal) {
                        Goal.LOSE_WEIGHT -> "Похудение"
                        Goal.MAINTAIN    -> "Поддержание"
                        Goal.GAIN_WEIGHT -> "Набор массы"
                    },
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = Icons.Default.Bolt,
                    label = "Активность",
                    value = activityShortName(profile.activityLevel),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun NutriGoalsCard(
    profile: UserProfile,
    onEditClick: () -> Unit,
    isHintDismissed: Boolean,
    onDismissHint: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Суточные нормы",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                TextButton(onClick = onEditClick) {
                    Text("Изменить", style = MaterialTheme.typography.labelMedium)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutriItem("Калории", "${profile.caloriesGoal}", "ккал", MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                NutriItem("Белки", "${profile.proteinGoal}", "г", MaterialTheme.colorScheme.tertiary, Modifier.weight(1f))
                NutriItem("Углеводы", "${profile.carbsGoal}", "г", MaterialTheme.colorScheme.secondary, Modifier.weight(1f))
                NutriItem("Жиры", "${profile.fatGoal}", "г", MaterialTheme.colorScheme.error, Modifier.weight(1f))
            }

            if (!isHintDismissed) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(start = 14.dp, top = 10.dp, bottom = 10.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("💡", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Нормы пересчитываются при изменении параметров. Вы также можете задать их вручную — нажмите «Изменить»",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = onDismissHint) {
                            Text(
                                "✕",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NutriItem(
    label: String,
    value: String,
    unit: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = color.copy(alpha = 0.7f)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun SettingsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Настройки",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            SettingsRow(icon = "🔔", title = "Уведомления", subtitle = "Настроить напоминания")
            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f))
            SettingsRow(icon = "ℹ️", title = "О приложении", subtitle = "Версия и информация")
        }
    }
}

@Composable
private fun SettingsRow(icon: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        Text(text = "›", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
    }
}

@Composable
private fun EditParamsSheet(
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

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f)
            .imePadding(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 8.dp,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Параметры",
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
                    onValueChange = { age = it.filter { c -> c.isDigit() } },
                    label = { Text("Возраст") },
                    suffix = { Text("лет") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it.filter { c -> c.isDigit() } },
                    label = { Text("Рост") },
                    suffix = { Text("см") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { c -> c.isDigit() } },
                    label = { Text("Вес") },
                    suffix = { Text("кг") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        item {
            Column {
                SectionLabel("Пол")
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Gender.entries.forEach { g ->
                        FilterChip(
                            selected = gender == g, onClick = { gender = g },
                            label = {
                                Text(
                                    text = if (g == Gender.MALE) "Мужской" else "Женский",
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        item {
            Column {
                SectionLabel("Цель")
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Goal.entries.forEach { g ->
                        FilterChip(
                            selected = goal == g, onClick = { goal = g },
                            label = { Text(goalDisplayName(g), modifier = Modifier.padding(vertical = 6.dp)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    }
                }
            }
        }

        item {
            Column {
                SectionLabel("Уровень активности")
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    ActivityLevel.entries.forEach { a ->
                        FilterChip(
                            selected = activityLevel == a, onClick = { activityLevel = a },
                            label = { Text(activityDisplayName(a), modifier = Modifier.padding(vertical = 6.dp)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    }
                }
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
                ) { Text("Отмена") }
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
                ) { Text("Сохранить") }
            }
        }
    }
}

@Composable
private fun EditMacrosSheet(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int, Int) -> Unit
) {
    var calories by remember(profile) { mutableStateOf(profile.caloriesGoal.toString()) }
    var protein by remember(profile) { mutableStateOf(profile.proteinGoal.toString()) }
    var carbs by remember(profile) { mutableStateOf(profile.carbsGoal.toString()) }
    var fat by remember(profile) { mutableStateOf(profile.fatGoal.toString()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = WindowInsets
                .navigationBars
                .asPaddingValues()
                .calculateBottomPadding() + 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Суточные нормы",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Задайте значения вручную под свои цели",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        NutrientEditField("Калории", calories, "ккал") { calories = it }
        NutrientEditField("Белки", protein, "г") { protein = it }
        NutrientEditField("Углеводы", carbs, "г") { carbs = it }
        NutrientEditField("Жиры", fat, "г") { fat = it }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Отмена")
            }
            Button(
                onClick = {
                    onSave(
                        calories.toIntOrNull() ?: profile.caloriesGoal,
                        protein.toIntOrNull() ?: profile.proteinGoal,
                        carbs.toIntOrNull() ?: profile.carbsGoal,
                        fat.toIntOrNull() ?: profile.fatGoal
                    )
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                enabled = calories.isNotBlank() && protein.isNotBlank() && carbs.isNotBlank() && fat.isNotBlank()
            ) {
                Text("Сохранить")
            }
        }
    }
}

@Composable
private fun NutrientEditField(
    label: String,
    value: String,
    unit: String,
    onValueChange: (String) -> Unit
) {
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
            value = value,
            onValueChange = { onValueChange(it.filter { c -> c.isDigit() }) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            suffix = { Text(unit) },
            singleLine = true,
            modifier = Modifier.width(130.dp),
            shape = RoundedCornerShape(12.dp)
        )
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

private fun activityShortName(level: ActivityLevel) = when (level) {
    ActivityLevel.SEDENTARY   -> "Минимум"
    ActivityLevel.LIGHT       -> "Лёгкая"
    ActivityLevel.MODERATE    -> "Средняя"
    ActivityLevel.ACTIVE      -> "Высокая"
    ActivityLevel.VERY_ACTIVE -> "Макс"
}

private fun activityDisplayName(level: ActivityLevel) = when (level) {
    ActivityLevel.SEDENTARY   -> "Сидячий образ жизни"
    ActivityLevel.LIGHT       -> "Лёгкая активность (1–3 раза в неделю)"
    ActivityLevel.MODERATE    -> "Умеренная активность (3–5 раз)"
    ActivityLevel.ACTIVE      -> "Высокая активность (6–7 раз)"
    ActivityLevel.VERY_ACTIVE -> "Очень высокая (спортсмен)"
}

private fun goalDisplayName(goal: Goal) = when (goal) {
    Goal.LOSE_WEIGHT -> "Похудение"
    Goal.MAINTAIN    -> "Поддержание веса"
    Goal.GAIN_WEIGHT -> "Набор массы"
}
package com.jacqulin.calcalc.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { ProfileHeader(uiState.userProfile, onEditClick = viewModel::showEditDialog) }
                item { QuickActionsCard() }
                item { SettingsSection() }
            }
        }

        if (uiState.showEditDialog) {
            EditProfileDialog(
                profile = uiState.userProfile,
                onDismiss = viewModel::hideEditDialog,
                onSave = viewModel::updateProfile
            )
        }

        if (uiState.showStatsDialog) {
            StatsDetailDialog(
                stats = uiState.weeklyStats,
                onDismiss = viewModel::hideStatsDialog
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    profile: UserProfile,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "${profile.age} лет • ${profile.activityLevel}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Стрик
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔥",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${profile.streak} дней подряд",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Редактировать профиль")
                }
            }
        }
    }
}

@Composable
private fun QuickActionsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Быстрые действия",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    title = "Дневник питания",
                    description = "Посмотреть записи",
                    icon = "📝",
                    modifier = Modifier.weight(1f)
                ) {
                    // TODO: Navigate to food diary
                }

                QuickActionButton(
                    title = "Добавить вес",
                    description = "Обновить прогресс",
                    icon = "⚖️",
                    modifier = Modifier.weight(1f)
                ) {
                    // TODO: Show weight input dialog
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionButton(
                    title = "Цели",
                    description = "Настроить",
                    icon = "🎯",
                    modifier = Modifier.weight(1f)
                ) {
                    // TODO: Navigate to goals settings
                }

                QuickActionButton(
                    title = "Фото прогресса",
                    description = "Сделать фото",
                    icon = "📸",
                    modifier = Modifier.weight(1f)
                ) {
                    // TODO: Open camera for progress photo
                }
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    description: String,
    icon: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SettingsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Настройки",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingItem(
                title = "Уведомления",
                description = "Настроить напоминания",
                icon = "🔔"
            ) {
                // TODO: Navigate to notifications settings
            }

            SettingItem(
                title = "О приложении",
                description = "Версия и информация",
                icon = "ℹ️"
            ) {
                // TODO: Show about dialog
            }
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    description: String,
    icon: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Text(
            text = "›",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun EditProfileDialog(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (UserProfile) -> Unit
) {
    var editedProfile by remember { mutableStateOf(profile) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Редактировать профиль",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.heightIn(max = 400.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = editedProfile.name,
                        onValueChange = { editedProfile = editedProfile.copy(name = it) },
                        label = { Text("Имя") }
                    )
                }

                item {
                    OutlinedTextField(
                        value = editedProfile.age.toString(),
                        onValueChange = {
                            editedProfile = editedProfile.copy(age = it.toIntOrNull() ?: 0)
                        },
                        label = { Text("Возраст") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                item {
                    OutlinedTextField(
                        value = String.format("%.1f", editedProfile.currentWeight),
                        onValueChange = {
                            editedProfile = editedProfile.copy(currentWeight = it.toFloatOrNull() ?: 0f)
                        },
                        label = { Text("Текущий вес (кг)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                item {
                    OutlinedTextField(
                        value = String.format("%.1f", editedProfile.targetWeight),
                        onValueChange = {
                            editedProfile = editedProfile.copy(targetWeight = it.toFloatOrNull() ?: 0f)
                        },
                        label = { Text("Целевой вес (кг)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(editedProfile) }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun StatsDetailDialog(
    stats: WeeklyStats,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Детальная статистика",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatsDetailItem(
                    label = "Выполнено дней",
                    value = "${stats.completedDays} из ${stats.totalDays}",
                    icon = "✅"
                )
                StatsDetailItem(
                    label = "Средние калории",
                    value = "${stats.avgCalories} ккал/день",
                    icon = "🔥"
                )
                StatsDetailItem(
                    label = "Средние стаканы воды",
                    value = "${stats.avgWater} стаканов/день",
                    icon = "💧"
                )
                StatsDetailItem(
                    label = "Потеря веса за неделю",
                    value = "-${stats.weightLoss} кг",
                    icon = "📉"
                )
                StatsDetailItem(
                    label = "Общая потеря веса",
                    value = "-${stats.totalWeightLoss} кг",
                    icon = "🎯"
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}

@Composable
private fun StatsDetailItem(
    label: String,
    value: String,
    icon: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
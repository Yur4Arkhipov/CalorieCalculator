package com.jacqulin.calcalc.feature.statistics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.roundToInt

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
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
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 60.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    StatisticsHeader(
                        selectedPeriod = uiState.selectedPeriod,
                        onPeriodChanged = viewModel::onPeriodChanged
                    )
                }
                item { WeeklyOverviewCard(uiState.weeklyAverage) }
                item { GoalsProgressCard(uiState.dailyStats) }
                item { CaloriesChart(uiState.dailyStats) }
                item { MacronutrientsDonutChart(uiState.dailyStats) }
                item {
                    DailyStatsGrid(
                        dailyStats = uiState.dailyStats,
                        onDayClick = { dayStats ->
                            viewModel.onDaySelected(dayStats)
                            viewModel.showEditDialog()
                        }
                    )
                }
            }
        }

        uiState.selectedDayStats?.let { selectedStats ->
            if (uiState.showEditDialog) {
                EditDayDialog(
                    dayStats = selectedStats,
                    onDismiss = viewModel::hideEditDialog,
                    onSave = viewModel::updateDayStats
                )
            }
        }
    }
}

@Composable
private fun StatisticsHeader(
    selectedPeriod: TimePeriod,
    onPeriodChanged: (TimePeriod) -> Unit
) {
    Column {
        Text(
            text = "Статистика",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(TimePeriod.entries) { period ->
                FilterChip(
                    selected = selectedPeriod == period,
                    onClick = { onPeriodChanged(period) },
                    label = { Text(period.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun WeeklyOverviewCard(weeklyAverage: WeeklyAverage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Обзор периода",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "📈",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatOverviewItem(
                    label = "Сред. калории",
                    value = "${weeklyAverage.avgCalories} ккал",
                    color = MaterialTheme.colorScheme.primary
                )
                StatOverviewItem(
                    label = "Идеальные дни",
                    value = "${weeklyAverage.perfectDays}/${weeklyAverage.totalDays}",
                    color = MaterialTheme.colorScheme.tertiary
                )
                StatOverviewItem(
                    label = "Средний вес",
                    value = "${String.format("%.1f", weeklyAverage.avgWeight)} кг",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
private fun StatOverviewItem(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun CaloriesChart(dailyStats: List<DailyStats>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "График калорий",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (dailyStats.isNotEmpty()) {
                    drawCaloriesChart(dailyStats)
                }
            }
        }
    }
}

@Composable
private fun MacronutrientsDonutChart(dailyStats: List<DailyStats>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Макронутриенты за период",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Расчет средних значений
                val avgProteins = dailyStats.map { it.proteins }.average().toFloat()
                val avgCarbs = dailyStats.map { it.carbs }.average().toFloat()
                val avgFats = dailyStats.map { it.fats }.average().toFloat()
                val total = avgProteins + avgCarbs + avgFats

                DonutChart(
                    data = listOf(
                        ChartData("Белки", avgProteins, Color(0xFFE57373)),
                        ChartData("Углеводы", avgCarbs, Color(0xFF64B5F6)),
                        ChartData("Жиры", avgFats, Color(0xFF81C784))
                    ),
                    modifier = Modifier.size(150.dp)
                )

                Column {
                    MacroLegendItem("Белки", avgProteins, total, Color(0xFFE57373))
                    Spacer(modifier = Modifier.height(8.dp))
                    MacroLegendItem("Углеводы", avgCarbs, total, Color(0xFF64B5F6))
                    Spacer(modifier = Modifier.height(8.dp))
                    MacroLegendItem("Жиры", avgFats, total, Color(0xFF81C784))
                }
            }
        }
    }
}

@Composable
private fun MacroLegendItem(name: String, value: Float, total: Float, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "${value.roundToInt()}г (${((value / total) * 100).roundToInt()}%)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DailyStatsGrid(
    dailyStats: List<DailyStats>,
    onDayClick: (DailyStats) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Детали по дням",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Нажмите для редактирования",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dailyStats.forEach { dayStat ->
                    DayStatsItem(
                        dayStats = dayStat,
                        onClick = { onDayClick(dayStat) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayStatsItem(
    dayStats: DailyStats,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = dayStats.date,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${dayStats.calories} ккал • ${dayStats.mealsCount} приёмов пищи",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Индикатор прогресса калорий
                CircularProgressIndicator(
                    progress = { dayStats.caloriesProgress.coerceIn(0f, 1f) },
                    modifier = Modifier.size(24.dp),
                    color = when {
                        dayStats.caloriesProgress < 0.8f -> MaterialTheme.colorScheme.error
                        dayStats.caloriesProgress > 1.2f -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    },
                    strokeWidth = 3.dp
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "✏️",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun EditDayDialog(
    dayStats: DailyStats,
    onDismiss: () -> Unit,
    onSave: (DailyStats) -> Unit
) {
    var editedStats by remember { mutableStateOf(dayStats) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Редактировать ${dayStats.date}",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = editedStats.calories.toString(),
                    onValueChange = {
                        editedStats = editedStats.copy(calories = it.toIntOrNull() ?: 0)
                    },
                    label = { Text("Калории") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = editedStats.water.toString(),
                    onValueChange = {
                        editedStats = editedStats.copy(water = it.toIntOrNull() ?: 0)
                    },
                    label = { Text("Стаканы воды") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                editedStats.weight?.let { weight ->
                    OutlinedTextField(
                        value = String.format("%.1f", weight),
                        onValueChange = {
                            editedStats = editedStats.copy(weight = it.toFloatOrNull())
                        },
                        label = { Text("Вес (кг)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(editedStats) }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun GoalsProgressCard(dailyStats: List<DailyStats>) {
    if (dailyStats.isEmpty()) return

    val recentStats = dailyStats.takeLast(7) // Берем последние 7 дней
    val avgCaloriesProgress = recentStats.map { it.caloriesProgress }.average().toFloat()
    val avgWaterProgress = recentStats.map { it.waterProgress }.average().toFloat()
    val perfectDaysCount = recentStats.count {
        it.caloriesProgress >= 0.9f && it.caloriesProgress <= 1.1f && it.waterProgress >= 1.0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Достижение целей 🎯",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    GoalProgressItem(
                        label = "Калории",
                        progress = avgCaloriesProgress.coerceIn(0f, 1f),
                        color = MaterialTheme.colorScheme.primary,
                        icon = "🔥"
                    )

                    GoalProgressItem(
                        label = "Вода",
                        progress = avgWaterProgress.coerceIn(0f, 1f),
                        color = MaterialTheme.colorScheme.secondary,
                        icon = "💧"
                    )

                    GoalProgressItem(
                        label = "Идеальные дни",
                        progress = perfectDaysCount / 7f,
                        color = MaterialTheme.colorScheme.tertiary,
                        icon = "⭐"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Идеальных дней за неделю:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$perfectDaysCount из 7",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalProgressItem(
    label: String,
    progress: Float,
    color: Color,
    icon: String
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "goal_progress"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(80.dp)
        ) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(80.dp),
                color = color,
                strokeWidth = 8.dp,
                trackColor = color.copy(alpha = 0.2f)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${(animatedProgress * 100).roundToInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// Функции для рисования графиков
private fun DrawScope.drawCaloriesChart(dailyStats: List<DailyStats>) {
    val maxCalories = dailyStats.maxOf { it.calories }.toFloat()
    val minCalories = dailyStats.minOf { it.calories }.toFloat()
    val calorieRange = maxCalories - minCalories

    val stepX = size.width / (dailyStats.size - 1).coerceAtLeast(1)
    val baseY = size.height * 0.9f

    // Рисуем линию цели калорий
    val goalY = baseY - ((2000f - minCalories) / calorieRange) * (baseY * 0.8f)
    drawLine(
        color = Color.Gray.copy(alpha = 0.5f),
        start = Offset(0f, goalY),
        end = Offset(size.width, goalY),
        strokeWidth = 2.dp.toPx(),
        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
    )

    // Рисуем точки и линии
    val path = Path()
    dailyStats.forEachIndexed { index, stat ->
        val x = index * stepX
        val y = baseY - ((stat.calories - minCalories) / calorieRange) * (baseY * 0.8f)

        if (index == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }

        // Рисуем точку
        drawCircle(
            color = Color(0xFF2196F3),
            radius = 6.dp.toPx(),
            center = Offset(x, y)
        )
    }

    // Рисуем линию графика
    drawPath(
        path = path,
        color = Color(0xFF2196F3),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
    )
}

data class ChartData(val label: String, val value: Float, val color: Color)

@Composable
private fun DonutChart(
    data: List<ChartData>,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val total = data.sumOf { it.value.toDouble() }.toFloat()
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 3
        val strokeWidth = radius * 0.6f

        var startAngle = -90f

        data.forEach { item ->
            val sweepAngle = (item.value / total) * 360f

            drawArc(
                color = item.color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth),
                topLeft = Offset(
                    centerX - radius,
                    centerY - radius
                ),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )

            startAngle += sweepAngle
        }
    }
}
package com.jacqulin.calcalc.feature.home.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.domain.model.MacroNutrients
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

private val ProteinColor = Color(0xFFE91E63)
private val CarbsColor = Color(0xFF2196F3)
private val FatsColor = Color(0xFFFF9800)
private const val SectorAngle = 120f
private const val StartAngle = -90f

@Composable
internal fun CaloriesSection(
    uiState: HomeUiState,
    onDetailClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Макронутриенты",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MacroRadialChart(
                        macros = uiState.todayMacros,
                        modifier = Modifier.size(120.dp)
                    )
                    CaloriesProgress(
                        consumed = uiState.consumedCalories,
                        goal = uiState.dailyCaloriesGoal,
                        modifier = Modifier.size(120.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                MacroPieChartLegend(uiState.todayMacros)
            }

            IconButton(
                onClick = onDetailClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Подробная информация",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun MacroRadialChart(
    macros: MacroNutrients,
    modifier: Modifier = Modifier
) {
    val proteinProgress by animateFloatAsState(
        targetValue = macros.protein / macros.proteinsGoal,
        label = "protein"
    )
    val carbsProgress by animateFloatAsState(
        targetValue = macros.carb / macros.carbsGoal,
        label = "carbs"
    )
    val fatsProgress by animateFloatAsState(
        targetValue = macros.fat / macros.fatsGoal,
        label = "fats"
    )
    fun overflowAlpha(progress: Float) =
        if (progress > 1f) 0.8f else 1f

    Canvas(modifier = modifier) {
        val baseRadius = size.minDimension / 2f
        val maxOverflow = baseRadius * 0.25f

        fun progressToRadius(progress: Float): Float =
            if (progress <= 1f) {
                baseRadius * progress
            } else {
                baseRadius + (progress - 1f).coerceAtMost(1f) * maxOverflow
            }

        drawCircle(
            color = Color.LightGray,
            radius = baseRadius,
            style = Stroke(width = 1.dp.toPx())
        )

        drawRadialSector(
            startAngle = StartAngle,
            sweepAngle = SectorAngle,
            radius = progressToRadius(proteinProgress),
            color = ProteinColor.copy(alpha = overflowAlpha(proteinProgress))
        )

        drawRadialSector(
            startAngle = StartAngle + SectorAngle,
            sweepAngle = SectorAngle,
            radius = progressToRadius(carbsProgress),
            color = CarbsColor.copy(alpha = overflowAlpha(carbsProgress))
        )

        drawRadialSector(
            startAngle = StartAngle + 2 * SectorAngle,
            sweepAngle = SectorAngle,
            radius = progressToRadius(fatsProgress),
            color = FatsColor.copy(alpha = overflowAlpha(fatsProgress))
        )
    }
}

private fun DrawScope.drawRadialSector(
    startAngle: Float,
    sweepAngle: Float,
    radius: Float,
    color: Color
) {
    val center = this.center
    val path = Path()

    path.moveTo(center.x, center.y)

    val steps = 50
    for (i in 0..steps) {
        val angle = Math.toRadians(
            (startAngle + sweepAngle * i / steps).toDouble()
        )

        val x = center.x + cos(angle).toFloat() * radius
        val y = center.y + sin(angle).toFloat() * radius

        path.lineTo(x, y)
    }

    path.close()

    drawPath(
        path = path,
        color = color
    )
}

@Composable
private fun MacroPieChartLegend(macros: MacroNutrients) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        MacroLegendItem(
            color = ProteinColor,
            name = "Белки",
            current = macros.protein,
            goal = macros.proteinsGoal.roundToInt()
        )
        MacroLegendItem(
            color = CarbsColor,
            name = "Углеводы",
            current = macros.carb,
            goal = macros.carbsGoal.roundToInt()
        )
        MacroLegendItem(
            color = FatsColor,
            name = "Жиры",
            current = macros.fat,
            goal = macros.fatsGoal.roundToInt()
        )
    }
}

@Composable
private fun MacroLegendItem(
    color: Color,
    name: String,
    current: Int,
    goal: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    color = color,
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${current}г/${goal}г",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun CaloriesProgress(
    consumed: Int,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = (consumed.toFloat() / goal).coerceAtMost(1f),
        label = "calories_progress"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 4.dp.toPx()

            drawArc(
                color = trackColor,
                startAngle = StartAngle,
                sweepAngle = 3 * SectorAngle,
                useCenter = false,
                style = Stroke(stroke)
            )

            drawArc(
                color = progressColor,
                startAngle = StartAngle,
                sweepAngle = 3 * SectorAngle * progress,
                useCenter = false,
                style = Stroke(
                    width = stroke,
                    cap = StrokeCap.Round
                )
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$consumed",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "из $goal",
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
            Text(
                text = "ккал",
                style = MaterialTheme.typography.labelSmall,
                color = textColor
            )
        }
    }
}
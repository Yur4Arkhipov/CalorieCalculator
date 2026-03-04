package com.jacqulin.calcalc.feature.home.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.icon.AppIcons
import com.jacqulin.calcalc.core.designsystem.theme.AppColors

@Composable
internal fun CaloriesSection(uiState: HomeUiState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CaloriesCard(
            consumed = uiState.consumedCalories,
            goal = uiState.dailyCaloriesGoal
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MacroCard(
                name = "Protein",
                current = uiState.todayMacros.protein,
                goal = uiState.todayMacros.proteinsGoal,
                color = AppColors.proteinMain,
                modifier = Modifier.weight(1f)
            )
            MacroCard(
                name = "Carbs",
                current = uiState.todayMacros.carb,
                goal = uiState.todayMacros.carbsGoal,
                color = AppColors.carbsMain,
                modifier = Modifier.weight(1f)
            )
            MacroCard(
                name = "Fat",
                current = uiState.todayMacros.fat,
                goal = uiState.todayMacros.fatsGoal,
                color = AppColors.fatMain,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CaloriesCard(
    consumed: Int,
    goal: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Calories",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = MaterialTheme.typography.displayMedium.fontFamily,
                                        fontSize = MaterialTheme.typography.displayMedium.fontSize,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("$consumed")
                                }
                                append(" ")
                                withStyle(
                                    style = SpanStyle(
                                        fontFamily = MaterialTheme.typography.headlineSmall.fontFamily,
                                        fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                        fontWeight = FontWeight.Normal,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                ) {
                                    append("/ $goal")
                                }
                            },
                            softWrap = false
                        )
                    }
                    val remaining = goal - consumed
                    Text(
                        text = if (remaining >= 0) "$remaining kcal left" else "${-remaining} kcal over",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                CircularProgressWithIcon(
                    current = consumed,
                    goal = goal,
                    color = AppColors.caloriesDark,
                    icon = AppIcons.calories(),
                    size = 100.dp
                )
            }
        }
    }
}

@Composable
private fun MacroCard(
    name: String,
    current: Int,
    goal: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val icon = when (name) {
        "Protein" -> AppIcons.calories()
        "Carbs" -> AppIcons.calories()
        "Fat" -> AppIcons.calories()
        else -> AppIcons.calories()
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$current",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )

            CircularProgressWithIcon(
                current = current,
                goal = goal,
                color = color,
                icon = icon,
                size = 70.dp
            )

            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun CircularProgressWithIcon(
    current: Int,
    goal: Int,
    color: Color,
    icon: Painter,
    size: Dp
) {
    val progress by animateFloatAsState(
        targetValue = if (goal > 0) (current.toFloat() / goal).coerceAtMost(1f) else 0f,
        label = "progress"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 6.dp.toPx()
            val trackColor = color.copy(alpha = 0.15f)

            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Icon(
            painter = icon,
            contentDescription = null
        )
    }
}
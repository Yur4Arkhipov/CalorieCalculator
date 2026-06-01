package com.jacqulin.calcalc.feature.home.ui.macrodetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.component.MealCard
import com.jacqulin.calcalc.core.designsystem.theme.AppColors
import com.jacqulin.calcalc.core.designsystem.theme.CaloriesDark
import com.jacqulin.calcalc.core.designsystem.theme.TextSecondary
import com.jacqulin.calcalc.core.designsystem.theme.TextTertiary
import com.jacqulin.calcalc.core.domain.model.Meal
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroDetailScreen(
    viewModel: MacroDetailViewModel = hiltViewModel(),
    onNavigateToMealDetail: (Int) -> Unit = { },
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
//    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current) + 12.dp,
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current) + 12.dp
                ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.size(26.dp),
                        onClick = onBackClick
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = stringResource(R.string.detail_back),
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.detail_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            item { CaloriesCard(uiState = uiState) }
            item { MacroProgressCards(uiState = uiState) }
            item {
                Text(
                    text = stringResource(R.string.detail_consumed_meals),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            item {
                MealCards(
                    meals = uiState.mealsToday,
//                    onMealClick = { viewModel.onEditMeal(it) }
                    onMealClick = { meal ->
                        onNavigateToMealDetail(meal.id)
                    }
                )
            }
            if (uiState.mealsToday.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.detail_no_meals),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(12.dp)) }
        }

//        if (uiState.isEditingSheetOpen && uiState.editingMeal != null) {
//            EditMealBottomSheet(
//                meal = uiState.editingMeal!!,
//                sheetState = sheetState,
//                onDismiss = { viewModel.onDismissEditMeal() },
//                onSave = { viewModel.onUpdateMeal(it) },
//                onDelete = { viewModel.onDeleteMeal(it) }
//            )
//        }
    }
}

@Composable
private fun CaloriesCard(uiState: MacroDetailUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.detail_daily_calories),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${uiState.consumedCalories}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = CaloriesDark
                    )
                    Text(
                        text = stringResource(R.string.detail_consumed),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val remaining = uiState.dailyCaloriesGoal - uiState.consumedCalories
                    Text(
                        text = if (remaining >= 0) "$remaining" else "0",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = stringResource(R.string.detail_remaining),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${uiState.dailyCaloriesGoal}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = CaloriesDark
                    )
                    Text(
                        text = stringResource(R.string.detail_goal),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun MacroProgressCards(uiState: MacroDetailUiState) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        MacroProgressCard(
            title = stringResource(R.string.proteins),
            current = uiState.todayMacros.protein,
            goal = uiState.todayMacros.proteinsGoal,
            color = AppColors.proteinMain
        )
        MacroProgressCard(
            title = stringResource(R.string.carbs),
            current = uiState.todayMacros.carb,
            goal = uiState.todayMacros.carbsGoal,
            color = AppColors.carbsMain
        )
        MacroProgressCard(
            title = stringResource(R.string.fats),
            current = uiState.todayMacros.fat,
            goal = uiState.todayMacros.fatsGoal,
            color = AppColors.fatMain
        )
    }
}

@Composable
private fun MacroProgressCard(
    title: String,
    current: Int,
    goal: Int,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape),
                        color = color
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = stringResource(R.string.detail_macro_progress_format, current, goal),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { (current.toFloat() / goal.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            val percentage = ((current.toFloat() / goal.toFloat()) * 100).roundToInt()
            Text(
                text = stringResource(R.string.detail_macro_percentage_format, percentage),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun MealCards(
    meals: List<Meal>,
    onMealClick: (Meal) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        meals.forEach { meal ->
            MealCard(
                meal = meal,
                onClick = { onMealClick(meal) }
            )
        }
    }
}
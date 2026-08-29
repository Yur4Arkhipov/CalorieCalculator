package com.jacqulin.calcalc.feature.home.ui.home.sections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.component.MealCard
import com.jacqulin.calcalc.core.designsystem.theme.SelectedToRemoveLightRed
import com.jacqulin.calcalc.core.designsystem.theme.White
import com.jacqulin.calcalc.core.domain.model.Meal
import kotlin.collections.forEach


@Composable
fun TodayMealsSection(
    meals: List<Meal>,
    selectedMealIds: Set<Int>,
    onDetailClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onMealClick: (Meal) -> Unit = {},
    onMealLongClick: (Meal) -> Unit = {},
    onNavigateToMealDetail: (Int) -> Unit
) {
    val isSelectionMode = selectedMealIds.isNotEmpty()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = if (isSelectionMode) 16.dp else 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text =  if (isSelectionMode) {
                    "Выделено: ${selectedMealIds.size}"
                } else {
                    stringResource(R.string.home_meals)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (isSelectionMode) {
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .widthIn(min = 80.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(SelectedToRemoveLightRed)
                        .clickable(onClick = onDeleteClick),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_remove),
                            contentDescription = "Удалить",
                            tint = White,
                            modifier = Modifier.height(12.dp)
                        )
                    }
                }
            } else {
                TextButton(
                    onClick = onDetailClick
                ) {
                    Text(stringResource(R.string.home_more_details))
                }
            }
        }

        if (meals.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_no_meals_add),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                meals.forEach { meal ->
                    MealCard(
                        meal = meal,
                        isSelectionEnabled = isSelectionMode,
                        isSelected = meal.id in selectedMealIds,
                        onClick = {
                            if (isSelectionMode) {
                                onMealClick(meal)
                            } else {
                                onNavigateToMealDetail(meal.id)
                            }
                        },
                        onLongClick = {
                            onMealLongClick(meal)
                        }
                    )
                }
            }
        }
    }
}
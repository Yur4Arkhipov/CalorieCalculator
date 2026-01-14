package com.jacqulin.calcalc.feature.home.ui.macrodetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.jacqulin.calcalc.core.domain.model.Meal
import kotlin.math.roundToInt

private val ProteinColor = Color(0xFFE91E63)
private val CarbsColor = Color(0xFF2196F3)
private val FatsColor = Color(0xFFFF9800)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MacroDetailScreen(
    viewModel: MacroDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { CaloriesCard(uiState = uiState) }
        item { MacroProgressCards(uiState = uiState) }
        item {
            Text(
                text = "Съеденные блюда за день",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        item {
            MealCards(
                meals = uiState.mealsToday,
                onMealClick = { viewModel.onEditMeal(it) }
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
                        text = "Пока не добавлено ни одного блюда",
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

    if (uiState.isEditingSheetOpen && uiState.editingMeal != null) {
        EditMealBottomSheet(
            meal = uiState.editingMeal!!,
            sheetState = sheetState,
            onDismiss = { viewModel.onDismissEditMeal() },
            onSave = { viewModel.onUpdateMeal(it) }
        )
    }
}

@Composable
private fun CaloriesCard(uiState: MacroDetailUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Калории",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
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
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Потреблено",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${uiState.dailyCaloriesGoal - uiState.consumedCalories}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Осталось",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${uiState.dailyCaloriesGoal}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Text(
                        text = "Цель",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
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
            title = "Белки",
            current = uiState.todayMacros.proteins.roundToInt(),
            goal = uiState.todayMacros.proteinsGoal.roundToInt(),
            color = ProteinColor
        )
        MacroProgressCard(
            title = "Углеводы",
            current = uiState.todayMacros.carbs.roundToInt(),
            goal = uiState.todayMacros.carbsGoal.roundToInt(),
            color = CarbsColor
        )
        MacroProgressCard(
            title = "Жиры",
            current = uiState.todayMacros.fats.roundToInt(),
            goal = uiState.todayMacros.fatsGoal.roundToInt(),
            color = FatsColor
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
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
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
                    text = "$current / $goal г",
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
                text = "$percentage% от дневной нормы",
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

@Composable
private fun MealCard(
    meal: Meal,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${meal.type.displayName} • ${meal.time}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = "Б: ${meal.proteins} г • Ж: ${meal.fats} г • У: ${meal.carbs} г",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Text(
                text = "${meal.calories} ккал",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMealBottomSheet(
    meal: Meal,
    sheetState: androidx.compose.material3.SheetState,
    onDismiss: () -> Unit,
    onSave: (Meal) -> Unit
) {
    var editedName by remember(meal) { mutableStateOf(meal.name) }
    var editedCalories by remember(meal) { mutableStateOf(meal.calories.toString()) }
    var editedProteins by remember(meal) { mutableStateOf(meal.proteins.toString()) }
    var editedCarbs by remember(meal) { mutableStateOf(meal.carbs.toString()) }
    var editedFats by remember(meal) { mutableStateOf(meal.fats.toString()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Редактировать блюдо",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Название блюда") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = editedCalories,
                onValueChange = { editedCalories = it.filter { char -> char.isDigit() } },
                label = { Text("Калории (ккал)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Макронутриенты",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = editedProteins,
                onValueChange = { editedProteins = it.filter { char -> char.isDigit() } },
                label = { Text("Белки (г)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = editedCarbs,
                onValueChange = { editedCarbs = it.filter { char -> char.isDigit() } },
                label = { Text("Углеводы (г)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = editedFats,
                onValueChange = { editedFats = it.filter { char -> char.isDigit() } },
                label = { Text("Жиры (г)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                        val updatedMeal = meal.copy(
                            name = editedName,
                            calories = editedCalories.toIntOrNull() ?: meal.calories,
                            proteins = editedProteins.toIntOrNull() ?: meal.proteins,
                            carbs = editedCarbs.toIntOrNull() ?: meal.carbs,
                            fats = editedFats.toIntOrNull() ?: meal.fats
                        )
                        onSave(updatedMeal)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = editedName.isNotBlank() &&
                             editedCalories.isNotBlank() &&
                             editedProteins.isNotBlank() &&
                             editedCarbs.isNotBlank() &&
                             editedFats.isNotBlank()
                ) {
                    Text("Сохранить")
                }
            }
        }
    }
}
package com.jacqulin.calcalc.feature.home.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.theme.White
import com.jacqulin.calcalc.core.domain.model.Meal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMealBottomSheet(
    meal: Meal,
    sheetState: androidx.compose.material3.SheetState,
    onDismiss: () -> Unit,
    onSave: (Meal) -> Unit,
    onDelete: (Meal) -> Unit = {}
) {
    var editedName by remember(meal) { mutableStateOf(meal.name) }
    var editedCalories by remember(meal) { mutableStateOf(meal.calories.toString()) }
    var editedProteins by remember(meal) { mutableStateOf(meal.proteins.toString()) }
    var editedCarbs by remember(meal) { mutableStateOf(meal.carbs.toString()) }
    var editedFats by remember(meal) { mutableStateOf(meal.fats.toString()) }
    var isFavorite by remember(meal) { mutableStateOf(meal.isFavorite) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

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
                .focusRequester(focusRequester)
                .focusable()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { focusRequester.requestFocus() }
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = editedName,
                    onValueChange = { newName ->
                        if (newName.length <= 30) {
                            editedName = newName
                        }
                    },
                    textStyle = MaterialTheme.typography.titleLarge,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusRequester.requestFocus()
                        }
                    ),
                    maxLines = 2,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                )
                Row {
                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            painter = if (isFavorite) painterResource(R.drawable.ic_favorite) else painterResource(R.drawable.ic_favorite),
                            contentDescription = if (isFavorite) "Убрать из избранного" else "В избранное",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_remove),
                            contentDescription = "Удалить",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

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
                            fats = editedFats.toIntOrNull() ?: meal.fats,
                            isFavorite = isFavorite
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

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Удалить блюдо?") },
            text = { Text("«${meal.name}» будет удалено из дневника.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(meal)
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Отмена")
                }
            },
            containerColor = White
        )
    }
}
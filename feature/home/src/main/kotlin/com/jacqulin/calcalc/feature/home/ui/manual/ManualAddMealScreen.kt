package com.jacqulin.calcalc.feature.home.ui.manual

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.component.AddMealSnackbar
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.util.effects.SnackbarMessageCode
import com.jacqulin.calcalc.core.util.effects.UiEffect
import com.jacqulin.calcalc.core.util.funtions.filterNumericInput
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualAddMealScreen(
    modifier: Modifier = Modifier,
    viewModel: ManualAddMealScreenViewModel = hiltViewModel(),
    onBackClick: () -> Unit = { },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val nameFocus = remember { FocusRequester() }
    val caloriesFocus = remember { FocusRequester() }
    val proteinsFocus = remember { FocusRequester() }
    val fatsFocus = remember { FocusRequester() }
    val carbsFocus = remember { FocusRequester() }

    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var snackbarIsError by remember { mutableStateOf(false) }
    var snackbarVisible by remember { mutableStateOf(false) }
    var snackbarJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is UiEffect.CloseScreen -> onBackClick()
                is UiEffect.ShowSnackbar -> {
                    snackbarJob?.cancel()

                    snackbarMessage = when (effect.messageCode) {
                        SnackbarMessageCode.MEAL_SAVED -> "Блюдо успешно сохранено!"
                        SnackbarMessageCode.MEAL_SAVE_ERROR -> "Ошибка сохранения"
                    }
                    snackbarIsError = effect.isError
                    snackbarVisible = true

                    snackbarJob = launch {
                        if (effect.isError) {
                            delay(3000)
                            snackbarVisible = false
                        } else {
                            delay(2000)
                            snackbarVisible = false
                            onBackClick()
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                AnimatedVisibility(
                    visible = snackbarVisible,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    snackbarMessage?.let { msg ->
                        AddMealSnackbar(
                            message = msg,
                            type = if (snackbarIsError) SnackbarMessageCode.MEAL_SAVE_ERROR
                                    else SnackbarMessageCode.MEAL_SAVED
                        )
                    }
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus(force = true)
                }
                .border(1.dp, Color.Red)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    modifier = Modifier.size(20.dp),
                    onClick = { onBackClick() }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = stringResource(R.string.home_manual_back),
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.home_manual_add_meal),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.widthIn(max = 360.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MealType.entries.forEach { mealType ->
                        MealTypeCard(
                            mealType = mealType,
                            isSelected = uiState.selectedMealType == mealType,
                            onClick = {
                                focusManager.clearFocus(force = true)
                                viewModel.onEvent(ManualAddMealEvent.MealTypeSelected(mealType))
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.mealName,
                onValueChange = { viewModel.onEvent(ManualAddMealEvent.MealNameChanged(it)) },
                label = { Text(text = stringResource(R.string.home_manual_field_product_name)) } ,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nameFocus),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { caloriesFocus.requestFocus() })
            )

            Text(
                text = stringResource(R.string.home_manual_nutrition_value),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutrientField(
                    label = stringResource(R.string.calories),
                    value = uiState.calories,
                    onValueChange = { input ->
                        val filtered = filterNumericInput(
                            input = input,
                            maxLength = 4,
                            maxValue = 2500
                        )
                        viewModel.onEvent(ManualAddMealEvent.CaloriesChanged(filtered))
                    },
                    suffix = stringResource(R.string.calories_suffix),
                    focusRequester = caloriesFocus,
                    imeAction = ImeAction.Next,
                    onImeAction = { proteinsFocus.requestFocus() },
                    modifier = Modifier.weight(1f)
                )
                NutrientField(
                    label = stringResource(R.string.proteins),
                    value = uiState.proteins,
                    onValueChange = { input ->
                        val filtered = filterNumericInput(
                            input = input,
                            maxLength = 3,
                            maxValue = 150,
                        )
                        viewModel.onEvent(ManualAddMealEvent.ProteinsChanged(filtered))
                    },
                    suffix = stringResource(R.string.weight_suffix),
                    focusRequester = proteinsFocus,
                    imeAction = ImeAction.Next,
                    onImeAction = { fatsFocus.requestFocus() },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutrientField(
                    label = stringResource(R.string.fats),
                    value = uiState.fats,
                    onValueChange = { input ->
                        val filtered = filterNumericInput(
                            input = input,
                            maxLength = 3,
                            maxValue = 150,
                        )
                        viewModel.onEvent(ManualAddMealEvent.FatsChanged(filtered))
                    },
                    suffix = stringResource(R.string.weight_suffix),
                    focusRequester = fatsFocus,
                    imeAction = ImeAction.Next,
                    onImeAction = { carbsFocus.requestFocus() },
                    modifier = Modifier.weight(1f)
                )
                NutrientField(
                    label = stringResource(R.string.carbs),
                    value = uiState.carbs,
                    onValueChange = { input ->
                        val filtered = filterNumericInput(
                            input = input,
                            maxLength = 3,
                            maxValue = 300,
                        )
                        viewModel.onEvent(ManualAddMealEvent.CarbsChanged(filtered))
                    },
                    suffix = stringResource(R.string.weight_suffix),
                    focusRequester = carbsFocus,
                    imeAction = ImeAction.Done,
                    onImeAction = { focusManager.clearFocus(force = true) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    focusManager.clearFocus(force = true)
                    viewModel.onEvent(ManualAddMealEvent.OnSaveClick)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = uiState.mealName.isNotBlank()
                    && uiState.calories.isNotBlank()
                    && uiState.proteins.isNotBlank()
                    && uiState.fats.isNotBlank()
                    && uiState.carbs.isNotBlank()
            ) {
                Text(
                    text = stringResource(R.string.save),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun NutrientField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
    focusRequester: FocusRequester,
    imeAction: ImeAction,
    onImeAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newVal ->
            if (newVal.isEmpty() || newVal.all { it.isDigit() }) {
                onValueChange(newVal)
            }
        },
        label = { Text(label) },
        modifier = modifier.focusRequester(focusRequester),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() }
        ),
        suffix = { Text(suffix) }
    )
}

@Composable
private fun MealTypeCard(
    mealType: MealType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = mealType.displayName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                textAlign = TextAlign.Center
            )
        }
    }
}
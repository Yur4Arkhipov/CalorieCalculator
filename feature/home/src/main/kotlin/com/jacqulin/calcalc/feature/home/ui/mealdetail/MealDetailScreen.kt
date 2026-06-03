package com.jacqulin.calcalc.feature.home.ui.mealdetail

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.designsystem.component.AddMealSnackbar
import com.jacqulin.calcalc.core.designsystem.component.FloatingActionButton
import com.jacqulin.calcalc.core.designsystem.component.MealTypeCard
import com.jacqulin.calcalc.core.designsystem.component.TopOverlay
import com.jacqulin.calcalc.core.domain.model.MealType
import com.jacqulin.calcalc.core.util.effects.SnackbarMessageCode
import com.jacqulin.calcalc.core.util.effects.UiEffect
import com.jacqulin.calcalc.feature.home.ui.review.components.IngredientCard
import com.jacqulin.calcalc.feature.home.ui.review.components.MealNameField
import com.jacqulin.calcalc.feature.home.ui.review.components.MealWeightField
import com.jacqulin.calcalc.feature.home.ui.review.components.NutrientsGrid
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun MealDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: MealDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val nameFocusRequester = remember { FocusRequester() }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val imageHeight = screenHeight * 0.35f

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
                        SnackbarMessageCode.CHANGES_SAVED -> "Изменения сохранены"
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
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .focusable()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { focusManager.clearFocus() }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(imageHeight)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                ) {
                    if (uiState.meal?.imageUri != null) {
                        AsyncImage(
                            model = File(uiState.meal?.imageUri!!),
                            contentDescription = uiState.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.ic_food),
                            contentDescription = stringResource(R.string.placeholder_image),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    }

                    TopOverlay(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = paddingValues.calculateTopPadding() + 16.dp,
                                start = paddingValues.calculateStartPadding(LocalLayoutDirection.current) + 12.dp,
                                end = paddingValues.calculateEndPadding(LocalLayoutDirection.current) + 12.dp
                            )
                            .align(Alignment.TopCenter),
                        isError = false,
                        isFavorite = uiState.isFavoriteMeal,
                        showFavoriteButton = true,
                        onFavoriteClick = { viewModel.onFavoriteChanged() },
                        onBackClick = onBackClick
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .height(20.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                                    )
                                )
                            )
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(
                            top = 24.dp,
                            bottom = paddingValues.calculateBottomPadding(),
                            start = paddingValues.calculateStartPadding(LocalLayoutDirection.current) + 12.dp,
                            end = paddingValues.calculateEndPadding(LocalLayoutDirection.current) + 12.dp
                        )
                        .fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
//                        MealReviewLoading(color = reviewLoadingColor)
                    } else if(uiState.isError) {
//                        TODO: add screen elements for error
                    } else {
                        MealNameField(
                            value = uiState.name,
                            onValueChanged = viewModel::onNameChange,
                            focusManager = focusManager,
                            nameFocusRequester = nameFocusRequester,
                            keyboardController = keyboardController
                        )

                        Spacer(modifier = Modifier.height(8.dp))

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
                                            viewModel.onMealTypeSelected(mealType)
                                        },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        MealWeightField(
                            weight = uiState.weight,
                            onWeightChange = viewModel::onWeightChanged
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        NutrientsGrid(
                            calories = uiState.calories,
                            protein = uiState.proteins,
                            fat = uiState.fats,
                            carbs = uiState.carbs,
                            onCaloriesChange = viewModel::onCaloriesChanged,
                            onProteinChange = viewModel::onProteinsChanged,
                            onFatChange = viewModel::onFatsChanged,
                            onCarbsChange = viewModel::onCarbsChanged
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = stringResource(R.string.review_meal_ingredients))

                        Spacer(modifier = Modifier.height(8.dp))

                        if (uiState.ingredients.isNotEmpty()) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                uiState.ingredients.forEach { ingredient ->
                                    IngredientCard(ingredient)
                                }
                            }
                        } else {
                            Text(
                                text = stringResource(R.string.detail_no_ingredients),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            if (uiState.isSaveIconEnable) {
                FloatingActionButton(
                    icon = painterResource(R.drawable.ic_check),
                    contentDescription = stringResource(R.string.save),
                    onClick = { viewModel.onSaveUpdatedMeal() },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }
    }
}
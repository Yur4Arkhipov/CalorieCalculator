package com.jacqulin.calcalc.feature.home.ui.review

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.jacqulin.calcalc.core.designsystem.component.AddMealSnackbar
import com.jacqulin.calcalc.core.designsystem.theme.reviewLoadingColor
import com.jacqulin.calcalc.core.util.effects.SnackbarMessageCode
import com.jacqulin.calcalc.core.util.effects.UiEffect
import com.jacqulin.calcalc.feature.home.ui.review.components.IngredientCard
import com.jacqulin.calcalc.feature.home.ui.review.components.MealNameField
import com.jacqulin.calcalc.feature.home.ui.review.components.MealReviewLoading
import com.jacqulin.calcalc.feature.home.ui.review.components.MealWeightField
import com.jacqulin.calcalc.feature.home.ui.review.components.NutrientsGrid
import com.jacqulin.calcalc.feature.home.ui.review.components.TopOverlay
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ConfigurationScreenWidthHeight")
@Composable
fun MealReviewScreen(
    modifier: Modifier = Modifier,
    viewModel: MealReviewScreenViewModel = hiltViewModel(),
    filePath: String,
    onBackClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val file = remember(filePath) { File(filePath) }

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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .padding(
//                    horizontal = paddingValues.calculateStartPadding(LocalLayoutDirection.current) - 12.dp
//                )
                .background(Color(0xFFFCFCFD))
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
                    AsyncImage(
                        model = file,
                        contentDescription = uiState.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )

                    TopOverlay(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter),
                        onSaveClick = { viewModel.saveMeal() },
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
                                        Color(0xFFFCFCFD).copy(alpha = 0.9f),
                                        Color(0xFFFCFCFD).copy(alpha = 0.95f)
                                    ),
                                    startY = 0f,
                                    endY = 60f
                                )
                            )
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                        .fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        MealReviewLoading(color = reviewLoadingColor)
                    } else {
                        MealNameField(
                            value = uiState.name,
                            onValueChanged = viewModel::onNameChange,
                            focusManager = focusManager,
                            nameFocusRequester = nameFocusRequester,
                            keyboardController = keyboardController
                        )

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

                        Text(text = "Ингредиенты")

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            uiState.ingredients.forEach { ingredient ->
                                IngredientCard(ingredient)
                            }
                        }
                    }
                }
            }
        }
    }
}
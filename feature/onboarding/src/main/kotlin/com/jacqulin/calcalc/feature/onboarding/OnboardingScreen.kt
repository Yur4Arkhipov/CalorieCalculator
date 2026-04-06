package com.jacqulin.calcalc.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jacqulin.calcalc.core.domain.model.ActivityLevel
import com.jacqulin.calcalc.core.domain.model.Gender
import com.jacqulin.calcalc.core.domain.model.Goal
import io.github.plovotok.wheelpicker.OverlayConfiguration
import io.github.plovotok.wheelpicker.WheelPicker
import io.github.plovotok.wheelpicker.rememberWheelPickerState
import kotlinx.coroutines.delay

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    var direction by rememberSaveable { mutableIntStateOf(1) }
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(state.currentPage) {
        isAnimating = true
        delay(650)
        isAnimating = false
    }

    val lastDataPage = 6
    val loadingPage = 7
    val resultPage = 8

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
    ) {
        if (state.currentPage <= lastDataPage) {
            OnboardingProgressIndicator(
                currentPage = state.currentPage,
                totalPages = lastDataPage + 1,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            Spacer(Modifier.height(8.dp + 16.dp + 8.dp))
        }

        AnimatedContent(
            targetState = state.currentPage,
            transitionSpec = {
                val slide = tween<IntOffset>(durationMillis = 650)
                val fade = tween<Float>(durationMillis = 500)
                if (direction > 0) {
                    slideInHorizontally(slide) { it } + fadeIn(fade) togetherWith
                        slideOutHorizontally(slide) { -it } + fadeOut(fade)
                } else {
                    slideInHorizontally(slide) { -it } + fadeIn(fade) togetherWith
                        slideOutHorizontally(slide) { it } + fadeOut(fade)
                }
            },
            modifier = Modifier.weight(1f),
            label = "onboarding_page"
        ) { page ->
            when (page) {
                0 -> WelcomePage()
                1 -> GenderPage(
                    selectedGender = state.gender,
                    onGenderSelected = { viewModel.onEvent(OnboardingEvent.UpdateGender(it)) }
                )
                2 -> GoalPage(
                    selectedGoal = state.goal,
                    onGoalSelected = { viewModel.onEvent(OnboardingEvent.UpdateGoal(it)) }
                )
                3 -> HeightPage(
                    selectedHeight = state.height,
                    onHeightSelected = { viewModel.onEvent(OnboardingEvent.UpdateHeight(it)) }
                )
                4 -> WeightPage(
                    selectedWeight = state.weight,
                    onWeightSelected = { viewModel.onEvent(OnboardingEvent.UpdateWeight(it)) }
                )
                5 -> AgePage(
                    selectedAge = state.age,
                    onAgeSelected = { viewModel.onEvent(OnboardingEvent.UpdateAge(it)) }
                )
                6 -> ActivityLevelPage(
                    selectedLevel = state.activityLevel,
                    onLevelSelected = { viewModel.onEvent(OnboardingEvent.UpdateActivityLevel(it)) }
                )
                loadingPage -> AnalyzingPage(
                    onFinished = {
                        direction = 1
                        viewModel.jumpToPage(resultPage)
                    }
                )
                resultPage -> ResultPage(
                    calories = state.calories,
                    protein = state.protein,
                    fat = state.fat,
                    carbs = state.carbs,
                    onCaloriesChange = { viewModel.onEvent(OnboardingEvent.UpdateCalories(it)) },
                    onProteinChange = { viewModel.onEvent(OnboardingEvent.UpdateProtein(it)) },
                    onFatChange = { viewModel.onEvent(OnboardingEvent.UpdateFat(it)) },
                    onCarbsChange = { viewModel.onEvent(OnboardingEvent.UpdateCarbs(it)) }
                )
            }
        }

        if (state.currentPage != loadingPage) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = state.currentPage > 0,
                    enter = fadeIn(tween(350)) + slideInHorizontally(tween(450)) { -it },
                    exit = fadeOut(tween(350)) + slideOutHorizontally(tween(450)) { -it }
                ) {
                    OutlinedButton(
                        onClick = {
                            if (isAnimating) return@OutlinedButton
                            direction = -1
                            if (state.currentPage == resultPage) {
                                direction = -1
                                viewModel.jumpToPage(lastDataPage)
                            } else {
                                viewModel.onEvent(OnboardingEvent.PreviousPage)
                            }
                        }
                    ) { Text("Назад") }
                }

                if (state.currentPage == 0) Spacer(Modifier.width(1.dp))

                Button(
                    onClick = {
                        if (isAnimating) return@Button
                        when (state.currentPage) {
                            lastDataPage -> {
                                // Запускаем расчёт и переходим на загрузку
                                direction = 1
                                viewModel.calculateAndGoToLoading()
                            }
                            resultPage -> viewModel.onEvent(OnboardingEvent.Complete)
                            else -> {
                                direction = 1
                                viewModel.onEvent(OnboardingEvent.NextPage)
                            }
                        }
                    }
                ) {
                    Text(if (state.currentPage == resultPage) "Начать!" else "Далее")
                }
            }
        } else {
            Spacer(Modifier.height(68.dp))
        }
    }
}

@Composable
private fun OnboardingProgressIndicator(
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(
                        width = if (index == currentPage) 24.dp else 8.dp,
                        height = 8.dp
                    )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = MaterialTheme.shapes.small,
                    color = if (index <= currentPage)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ) {}
            }
        }
    }
}

@Composable
private fun SelectionCard(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .height(72.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Добро пожаловать!", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))
        Text(
            "Давайте настроим приложение под вас",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun GenderPage(
    selectedGender: Gender?,
    onGenderSelected: (Gender) -> Unit
) {
    val options = listOf(
        Gender.MALE to "Мужской",
        Gender.FEMALE to "Женский"
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ваш пол", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        options.forEach { (gender, label) ->
            SelectionCard(
                label = label,
                selected = selectedGender == gender,
                onClick = { onGenderSelected(gender) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun AgePage(
    selectedAge: Int?,
    onAgeSelected: (Int) -> Unit
) {

    val list = remember { (10..100).toList() }

    val initialIndex = remember(selectedAge) {
        ((selectedAge ?: 20) - 10).coerceIn(0, list.lastIndex)
    }

    val pickerState = rememberWheelPickerState(
        initialIndex = initialIndex,
        infinite = false
    )

    val selectedIndex by pickerState.selectedItemState(list.size)

    LaunchedEffect(selectedIndex) {
        onAgeSelected(list[selectedIndex])
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Сколько вам лет?",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        WheelPicker(
            data = list,
            state = pickerState,
            overlay = OverlayConfiguration.create(
                scrimColor = Color.Transparent,
                focusColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            ),
            itemContent = { index ->

                val isSelected = index == selectedIndex

                Text(
                    text = "${list[index]}",
                    fontSize = if (isSelected) 24.sp else 18.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        )
    }
}

@Composable
private fun HeightPage(
    selectedHeight: Int?,
    onHeightSelected: (Int) -> Unit
) {
    val list = remember { (100..250).map { it } }

    val initialIndex = remember(selectedHeight) {
        ((selectedHeight ?: 165) - 100).coerceIn(0, list.lastIndex)
    }

    val pickerState = rememberWheelPickerState(
        initialIndex = initialIndex,
        infinite = false
    )

    val selectedIndex by pickerState.selectedItemState(list.size)

    LaunchedEffect(selectedIndex) {
        onHeightSelected(list[selectedIndex])
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ваш рост",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        WheelPicker(
            data = list,
            state = pickerState,
            overlay = OverlayConfiguration.create(
                scrimColor = Color.Transparent,
                focusColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            ),
            itemContent = { index ->
                val isSelected = index == selectedIndex
                Text(
                    text = "${list[index]} см",
                    fontSize = if (isSelected) 24.sp else 18.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        )
    }
}

@Composable
private fun WeightPage(
    selectedWeight: Int?,
    onWeightSelected: (Int) -> Unit
) {
    val list = remember { (30..200).map { it } }

    val initialIndex = remember(selectedWeight) {
        ((selectedWeight ?: 65) - 30).coerceIn(0, list.lastIndex)
    }

    val pickerState = rememberWheelPickerState(
        initialIndex = initialIndex,
        infinite = false
    )

    val selectedIndex by pickerState.selectedItemState(list.size)

    LaunchedEffect(selectedIndex) {
        onWeightSelected(list[selectedIndex])
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ваш вес",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        WheelPicker(
            data = list,
            state = pickerState,
            overlay = OverlayConfiguration.create(
                scrimColor = Color.Transparent,
                focusColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            ),
            itemContent = { index ->
                val isSelected = index == selectedIndex
                Text(
                    text = "${list[index]} кг",
                    fontSize = if (isSelected) 24.sp else 18.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
        )
    }
}

@Composable
private fun GoalPage(
    selectedGoal: Goal?,
    onGoalSelected: (Goal) -> Unit
) {
    val options = listOf(
        Goal.LOSE_WEIGHT to "Похудеть",
        Goal.MAINTAIN to "Поддержать вес",
        Goal.GAIN_WEIGHT to "Набрать массу"
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ваша цель", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        options.forEach { (goal, label) ->
            SelectionCard(
                label = label,
                selected = selectedGoal == goal,
                onClick = { onGoalSelected(goal) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun ActivityLevelPage(
    selectedLevel: ActivityLevel?,
    onLevelSelected: (ActivityLevel) -> Unit
) {
    val options = listOf(
        Triple(ActivityLevel.SEDENTARY,  "Сидячий образ жизни",    "Минимум движений, офисная работа"),
        Triple(ActivityLevel.LIGHT,      "Лёгкая активность",       "Спорт 1–3 раза в неделю"),
        Triple(ActivityLevel.MODERATE,   "Средняя активность",      "Тренировки 3–5 раз в неделю"),
        Triple(ActivityLevel.ACTIVE,     "Высокая активность",      "Интенсивные тренировки 6–7 раз в неделю"),
        Triple(ActivityLevel.VERY_ACTIVE,"Экстремальная активность","Ежедневные тяжёлые тренировки или физическая работа")
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Уровень активности", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        options.forEach { (level, label, description) ->
            ActivityCard(
                label = label,
                description = description,
                selected = selectedLevel == level,
                onClick = { onLevelSelected(level) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
            )
        }
    }
}

@Composable
private fun ActivityCard(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected)
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun AnalyzingPage(onFinished: () -> Unit) {
    val steps = listOf(
        "Анализируем ваш профиль...",
        "Вычисляем базовый обмен веществ...",
        "Подбираем калорийность...",
        "Рассчитываем баланс БЖУ...",
        "Готовим рекомендации..."
    )

    var currentStep by remember { mutableIntStateOf(0) }
    var stepVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        steps.forEachIndexed { index, _ ->
            currentStep = index
            stepVisible = true
            delay(900)
            stepVisible = false
            delay(200)
        }
        delay(300)
        onFinished()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "loader")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Настраиваем под вас",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(48.dp))

        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            strokeWidth = 5.dp
        )

        Spacer(Modifier.height(40.dp))

        AnimatedContent(
            targetState = if (stepVisible) steps.getOrElse(currentStep) { "" } else "",
            transitionSpec = {
                fadeIn(tween(200)) togetherWith fadeOut(tween(200))
            },
            label = "step_text"
        ) { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun ResultPage(
    calories: Int,
    protein: Int,
    fat: Int,
    carbs: Int,
    onCaloriesChange: (Int) -> Unit,
    onProteinChange: (Int) -> Unit,
    onFatChange: (Int) -> Unit,
    onCarbsChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ваша норма",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Можете скорректировать значения под себя",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp, bottom = 32.dp)
        )

        NutrientField(
            label = "Калории",
            value = calories,
            unit = "ккал",
            onValueChange = onCaloriesChange
        )
        Spacer(Modifier.height(12.dp))
        NutrientField(
            label = "Белки",
            value = protein,
            unit = "г",
            onValueChange = onProteinChange
        )
        Spacer(Modifier.height(12.dp))
        NutrientField(
            label = "Жиры",
            value = fat,
            unit = "г",
            onValueChange = onFatChange
        )
        Spacer(Modifier.height(12.dp))
        NutrientField(
            label = "Углеводы",
            value = carbs,
            unit = "г",
            onValueChange = onCarbsChange
        )
    }
}

@Composable
private fun NutrientField(
    label: String,
    value: Int,
    unit: String,
    onValueChange: (Int) -> Unit
) {
    var text by remember(value) { mutableStateOf(value.toString()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            value = text,
            onValueChange = { input ->
                text = input
                input.toIntOrNull()?.let { onValueChange(it) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            suffix = { Text(unit) },
            singleLine = true,
            modifier = Modifier.width(130.dp)
        )
    }
}
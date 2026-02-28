package com.jacqulin.calcalc.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
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
        delay(650) // длительность slideInHorizontally
        isAnimating = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp)
    ) {
        OnboardingProgressIndicator(
            currentPage = state.currentPage,
            totalPages = state.totalPages,
            modifier = Modifier.padding(vertical = 16.dp)
        )

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
            }
        }

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
                        viewModel.onEvent(OnboardingEvent.PreviousPage)
                    }
                ) {
                    Text("Назад")
                }
            }

            // Заглушка чтобы кнопка "Далее" не прыгала влево когда "Назад" скрыта
            if (state.currentPage == 0) {
                Spacer(Modifier.width(1.dp))
            }

            Button(
                onClick = {
                    if (isAnimating) return@Button
                    if (state.currentPage == state.totalPages - 1) {
                        viewModel.onEvent(OnboardingEvent.Complete)
                    } else {
                        direction = 1
                        viewModel.onEvent(OnboardingEvent.NextPage)
                    }
                }
            ) {
                Text(if (state.currentPage == state.totalPages - 1) "Готово" else "Далее")
            }
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

// ── Карточка-выбор ────────────────────────────────────────────────────────────

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

// ── Страницы ──────────────────────────────────────────────────────────────────

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
    selectedHeight: Float?,
    onHeightSelected: (Float) -> Unit
) {
    val list = remember { (100..250).map { it.toFloat() } }

    val initialIndex = remember(selectedHeight) {
        ((selectedHeight?.toInt() ?: 165) - 100).coerceIn(0, list.lastIndex)
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
                    text = "${list[index].toInt()} см",
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
    selectedWeight: Float?,
    onWeightSelected: (Float) -> Unit
) {
    val list = remember { (30..200).map { it.toFloat() } }

    val initialIndex = remember(selectedWeight) {
        ((selectedWeight?.toInt() ?: 65) - 30).coerceIn(0, list.lastIndex)
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
                    text = "${list[index].toInt()} кг",
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
        ActivityLevel.SEDENTARY to "Сидячий образ жизни",
        ActivityLevel.LIGHT to "Легкая активность",
        ActivityLevel.MODERATE to "Умеренная активность",
        ActivityLevel.ACTIVE to "Активный образ жизни",
        ActivityLevel.VERY_ACTIVE to "Очень активный"
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Уровень активности", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        options.forEach { (level, label) ->
            SelectionCard(
                label = label,
                selected = selectedLevel == level,
                onClick = { onLevelSelected(level) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            )
        }
    }
}
package com.jacqulin.calcalc.feature.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jacqulin.calcalc.core.domain.model.ActivityLevel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState { state.totalPages }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != state.currentPage) {
            if (pagerState.currentPage > state.currentPage) {
                viewModel.onEvent(OnboardingEvent.NextPage)
            } else {
                viewModel.onEvent(OnboardingEvent.PreviousPage)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { viewModel.onEvent(OnboardingEvent.Skip) }) {
                Text("Пропустить")
            }
        }

        OnboardingProgressIndicator(
            currentPage = state.currentPage,
            totalPages = state.totalPages,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Контент страниц с HorizontalPager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> WelcomePage()
                1 -> AgePage(
                    age = state.age,
                    onAgeChange = { viewModel.onEvent(OnboardingEvent.UpdateAge(it)) }
                )
                2 -> BodyMetricsPage(
                    height = state.height,
                    weight = state.weight,
                    onHeightChange = { viewModel.onEvent(OnboardingEvent.UpdateHeight(it)) },
                    onWeightChange = { viewModel.onEvent(OnboardingEvent.UpdateWeight(it)) }
                )
                3 -> ActivityLevelPage(
                    selectedLevel = state.activityLevel,
                    onLevelSelected = { viewModel.onEvent(OnboardingEvent.UpdateActivityLevel(it)) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (state.currentPage > 0) {
                OutlinedButton(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(state.currentPage - 1)
                        }
                    }
                ) {
                    Text("Назад")
                }
            } else {
                Spacer(Modifier.width(1.dp))
            }

            Button(
                onClick = {
                    if (state.currentPage == state.totalPages - 1) {
                        viewModel.onEvent(OnboardingEvent.Complete)
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(state.currentPage + 1)
                        }
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
private fun AgePage(age: Int?, onAgeChange: (Int) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Сколько вам лет?", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = age.toString(),
            onValueChange = { if (it.all { c -> c.isDigit() }) onAgeChange(it.toInt()) },
            label = { Text("Возраст") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
}

@Composable
private fun BodyMetricsPage(
    height: Float?,
    weight: Float?,
    onHeightChange: (Float?) -> Unit,
    onWeightChange: (Float?) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ваши параметры", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = height.toString(),
            onValueChange = { newValue ->
                val parsed = newValue.toFloatOrNull()
                onHeightChange(parsed)
            },
            label = { Text("Рост (см)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = weight.toString(),
            onValueChange = { newValue ->
                val parsed = newValue.toFloatOrNull()
                onHeightChange(parsed)
            },
            label = { Text("Вес (кг)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
    }
}

@Composable
private fun ActivityLevelPage(
    selectedLevel: ActivityLevel?,
    onLevelSelected: (ActivityLevel) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Уровень активности", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        ActivityLevel.entries.forEach { level ->
            val label = when (level) {
                ActivityLevel.SEDENTARY -> "Сидячий образ жизни"
                ActivityLevel.LIGHT -> "Легкая активность"
                ActivityLevel.MODERATE -> "Умеренная активность"
                ActivityLevel.ACTIVE -> "Активный образ жизни"
                ActivityLevel.VERY_ACTIVE -> "Очень активный"
            }

            FilterChip(
                selected = selectedLevel == level,
                onClick = { onLevelSelected(level) },
                label = { Text(label) },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
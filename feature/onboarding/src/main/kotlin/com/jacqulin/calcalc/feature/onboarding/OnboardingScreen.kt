package com.jacqulin.calcalc.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Кнопка пропустить
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { viewModel.onEvent(OnboardingEvent.Skip) }) {
                Text("Пропустить")
            }
        }

        // Индикатор прогресса
        OnboardingProgressIndicator(
            currentPage = state.currentPage,
            totalPages = state.totalPages,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        // Контент страниц
        AnimatedContent(
            targetState = state.currentPage,
            modifier = Modifier.weight(1f),
            label = "page_content"
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

        // Навигационные кнопки
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (state.currentPage > 0) {
                OutlinedButton(onClick = { viewModel.onEvent(OnboardingEvent.PreviousPage) }) {
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
private fun AgePage(age: String, onAgeChange: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Сколько вам лет?", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = age,
            onValueChange = { if (it.all { c -> c.isDigit() }) onAgeChange(it) },
            label = { Text("Возраст") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )
    }
}

@Composable
private fun BodyMetricsPage(
    height: String,
    weight: String,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ваши параметры", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = height,
            onValueChange = onHeightChange,
            label = { Text("Рост (см)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = weight,
            onValueChange = onWeightChange,
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
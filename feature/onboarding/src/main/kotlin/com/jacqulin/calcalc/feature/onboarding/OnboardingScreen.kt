package com.jacqulin.calcalc.feature.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.feature.onboarding.pages.ActivityLevelPage
import com.jacqulin.calcalc.feature.onboarding.pages.AgePage
import com.jacqulin.calcalc.feature.onboarding.pages.AnalyzingPage
import com.jacqulin.calcalc.feature.onboarding.pages.GenderPage
import com.jacqulin.calcalc.feature.onboarding.pages.GoalPage
import com.jacqulin.calcalc.feature.onboarding.pages.HeightPage
import com.jacqulin.calcalc.feature.onboarding.pages.ResultPage
import com.jacqulin.calcalc.feature.onboarding.pages.WeightPage
import com.jacqulin.calcalc.feature.onboarding.pages.WelcomePage
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
                    ) { Text(stringResource(R.string.onboarding_back)) }
                }

                if (state.currentPage == 0) Spacer(Modifier.width(1.dp))

                Button(
                    onClick = {
                        if (isAnimating) return@Button
                        when (state.currentPage) {
                            lastDataPage -> {
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
                    Text( text = if (state.currentPage == resultPage)
                        stringResource(R.string.onboarding_start)
                    else
                        stringResource(R.string.onboarding_next)
                    )
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
    modifier: Modifier = Modifier,
    totalPages: Int
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
                ) { }
            }
        }
    }
}
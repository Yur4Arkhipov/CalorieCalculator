package com.jacqulin.calcalc.feature.onboarding.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R
import kotlinx.coroutines.delay

@Composable
fun AnalyzingPage(onFinished: () -> Unit) {
    val steps = listOf(
        stringResource(R.string.onboarding_analyzing_step_1),
        stringResource(R.string.onboarding_analyzing_step_2),
        stringResource(R.string.onboarding_analyzing_step_3),
        stringResource(R.string.onboarding_analyzing_step_4),
        stringResource(R.string.onboarding_analyzing_step_5)
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
            text = stringResource(R.string.onboarding_analyzing_title),
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
package com.jacqulin.calcalc.feature.onboarding.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.domain.model.Goal


@Composable
fun GoalPage(
    selectedGoal: Goal?,
    onGoalSelected: (Goal) -> Unit
) {
    val options = listOf(
        Goal.LOSE_WEIGHT to stringResource(R.string.onboarding_goal_lose_weight),
        Goal.MAINTAIN to stringResource(R.string.onboarding_goal_maintain),
        Goal.GAIN_WEIGHT to stringResource(R.string.onboarding_goal_gain_weight)
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.onboarding_goal_title),
            style = MaterialTheme.typography.headlineMedium
        )
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
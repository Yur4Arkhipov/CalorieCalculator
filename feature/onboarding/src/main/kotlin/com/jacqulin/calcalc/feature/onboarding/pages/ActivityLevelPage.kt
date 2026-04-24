package com.jacqulin.calcalc.feature.onboarding.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.domain.model.ActivityLevel

@Composable
fun ActivityLevelPage(
    selectedLevel: ActivityLevel?,
    onLevelSelected: (ActivityLevel) -> Unit
) {
    val options = listOf(
        Triple(
            ActivityLevel.SEDENTARY,
            stringResource(R.string.onboarding_activity_sedentary_title),
            stringResource(R.string.onboarding_activity_sedentary_desc)
        ),
        Triple(
            ActivityLevel.LIGHT,
            stringResource(R.string.onboarding_activity_light_title),
            stringResource(R.string.onboarding_activity_light_desc)
        ),
        Triple(
            ActivityLevel.MODERATE,
            stringResource(R.string.onboarding_activity_moderate_title),
            stringResource(R.string.onboarding_activity_moderate_desc)
        ),
        Triple(
            ActivityLevel.ACTIVE,
            stringResource(R.string.onboarding_activity_active_title),
            stringResource(R.string.onboarding_activity_active_desc)
        ),
        Triple(
            ActivityLevel.VERY_ACTIVE,
            stringResource(R.string.onboarding_activity_very_active_title),
            stringResource(R.string.onboarding_activity_very_active_desc)
        )
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.onboarding_activity_title),
            style = MaterialTheme.typography.headlineMedium
        )
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
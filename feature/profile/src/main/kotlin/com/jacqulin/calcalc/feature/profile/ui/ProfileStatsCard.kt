package com.jacqulin.calcalc.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jacqulin.calcalc.core.designsystem.R
import com.jacqulin.calcalc.core.domain.model.ActivityLevel
import com.jacqulin.calcalc.core.domain.model.Gender
import com.jacqulin.calcalc.core.domain.model.Goal
import com.jacqulin.calcalc.core.domain.model.UserProfile

@Composable
fun ProfileStatsCard(
    profile: UserProfile,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.profile_my_parameters),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.profile_edit))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatItem(
                    icon = painterResource(R.drawable.ic_person),
                    label = stringResource(R.string.profile_age),
                    value = stringResource(R.string.profile_age_suffix, profile.age),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = painterResource(R.drawable.ic_height),
                    label = stringResource(R.string.profile_height),
                    value = stringResource(R.string.profile_height_suffix, profile.height),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = painterResource(R.drawable.ic_weight),
                    label = stringResource(R.string.profile_weight),
                    value = stringResource(R.string.profile_weight, profile.weight),
                    modifier = Modifier.weight(1f)
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatItem(
                    icon = painterResource(R.drawable.ic_genders),
                    label = stringResource(R.string.profile_gender),
                    value = if (profile.gender == Gender.MALE)
                        stringResource(R.string.profile_gender_male)
                    else
                        stringResource(R.string.profile_gender_female),
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = painterResource(R.drawable.ic_target),
                    label = stringResource(R.string.profile_goal),
                    value = when (profile.goal) {
                        Goal.LOSE_WEIGHT -> stringResource(R.string.profile_goal_lose)
                        Goal.MAINTAIN    -> stringResource(R.string.profile_goal_maintain)
                        Goal.GAIN_WEIGHT -> stringResource(R.string.profile_goal_gain)
                    },
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    icon = painterResource(R.drawable.ic_bolt),
                    label = stringResource(R.string.profile_activity),
                    value = activityShortName(profile.activityLevel),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: Painter,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun activityShortName(level: ActivityLevel) = when (level) {
    ActivityLevel.SEDENTARY   -> stringResource(R.string.profile_activity_short_sedentary)
    ActivityLevel.LIGHT       -> stringResource(R.string.profile_activity_short_light)
    ActivityLevel.MODERATE    -> stringResource(R.string.profile_activity_short_moderate)
    ActivityLevel.ACTIVE      -> stringResource(R.string.profile_activity_short_active)
    ActivityLevel.VERY_ACTIVE -> stringResource(R.string.profile_activity_short_max)
}
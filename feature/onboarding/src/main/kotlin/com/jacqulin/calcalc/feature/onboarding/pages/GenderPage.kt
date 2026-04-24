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
import com.jacqulin.calcalc.core.domain.model.Gender

@Composable
fun GenderPage(
    selectedGender: Gender?,
    onGenderSelected: (Gender) -> Unit
) {
    val options = listOf(
        Gender.MALE to stringResource(R.string.onboarding_gender_male),
        Gender.FEMALE to stringResource(R.string.onboarding_gender_female)
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.onboarding_your_gender),
            style = MaterialTheme.typography.headlineMedium
        )
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
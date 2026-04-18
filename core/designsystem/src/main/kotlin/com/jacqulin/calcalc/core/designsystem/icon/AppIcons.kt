package com.jacqulin.calcalc.core.designsystem.icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.jacqulin.calcalc.core.designsystem.R

object AppIcons {
    val Statistics = R.drawable.ic_bookmark
    val Home = R.drawable.ic_home_rounded
    val Profile = R.drawable.ic_person_rounded

    @Composable
    fun calories() = painterResource(R.drawable.outline_local_fire_department_24)
}
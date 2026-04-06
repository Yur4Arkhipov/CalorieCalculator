package com.jacqulin.calcalc.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.StackedLineChart
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.jacqulin.calcalc.core.designsystem.R

object AppIcons {
    val StatisticsSelected = Icons.Rounded.StackedLineChart
    val StatisticsUnselected = Icons.Outlined.StackedLineChart

    val HomeSelected = Icons.Rounded.Home
    val HomeUnselected = Icons.Outlined.Home

    val ProfileSelected = Icons.Rounded.Person
    val ProfileUnselected = Icons.Outlined.Person

    val Field1Selected = Icons.Filled.Build
    val Field1Unselected = Icons.Outlined.Build

    val Field2Selected = Icons.Filled.Build
    val Field2Unselected = Icons.Outlined.Build

    @Composable
    fun calories() = painterResource(R.drawable.outline_local_fire_department_24)
}
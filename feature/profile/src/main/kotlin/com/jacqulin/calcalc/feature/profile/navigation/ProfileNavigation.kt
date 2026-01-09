package com.jacqulin.calcalc.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.jacqulin.calcalc.feature.profile.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
data object ProfileRoute

@Serializable
data object ProfileBaseRoute

fun NavController.navigateToProfile(navOptions: NavOptions? = null) =
    navigate(ProfileRoute, navOptions)

fun NavGraphBuilder.profileScreen(
    onBackClick: () -> Unit
) {
    composable<ProfileRoute>() {
        ProfileScreen(onBackClick = onBackClick)
    }
}
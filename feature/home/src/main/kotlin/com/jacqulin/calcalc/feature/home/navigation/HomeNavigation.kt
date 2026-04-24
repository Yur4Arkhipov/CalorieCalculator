package com.jacqulin.calcalc.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.jacqulin.calcalc.feature.home.ui.aitext.AiMealDescriptionScreen
import com.jacqulin.calcalc.feature.home.ui.favorite.FavoriteMealChooseScreen
import com.jacqulin.calcalc.feature.home.ui.home.HomeScreen
import com.jacqulin.calcalc.feature.home.ui.macrodetail.MacroDetailScreen
import com.jacqulin.calcalc.feature.home.ui.manual.ManualAddMealScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object FavoriteMealChooseRoute

@Serializable
data object MacroDetailRoute

@Serializable
data object AiMealDescriptionRoute

@Serializable
data object ManualAddMealRoute

@Serializable
data object HomeBaseRoute

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(route = HomeRoute, navOptions)

fun NavController.navigateToFavoriteChoose() = navigate(route = FavoriteMealChooseRoute)

fun NavController.navigateToMacroDetail() = navigate(route = MacroDetailRoute)

fun NavController.navigateToAiMealDescription() = navigate(route = AiMealDescriptionRoute)

fun NavController.navigateToManualAddMeal() = navigate(route = ManualAddMealRoute)

fun NavGraphBuilder.homeSection(
    onNavigateToFavoriteChoose: () -> Unit,
    onNavigateToMacroDetail: () -> Unit,
    onNavigateToAiMealDescription: () -> Unit,
    onNavigateToManualAddMeal: () -> Unit,
    onBackClick: () -> Unit
) {
    navigation<HomeBaseRoute>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(
                onNavigateToAddFavoriteMeal = onNavigateToFavoriteChoose,
                onNavigateToMacroDetail = onNavigateToMacroDetail,
                onNavigateToAiMealDescription = onNavigateToAiMealDescription,
                onNavigateToManualAddMeal = onNavigateToManualAddMeal,
            )
        }

        composable<FavoriteMealChooseRoute> {
            FavoriteMealChooseScreen(onBackClick = onBackClick)
        }

        composable<MacroDetailRoute> {
            MacroDetailScreen(onBackClick = onBackClick)
        }

        composable<AiMealDescriptionRoute> {
            AiMealDescriptionScreen(onBackClick = onBackClick)
        }

        composable<ManualAddMealRoute> {
            ManualAddMealScreen(onBackClick = onBackClick)
        }
    }
}
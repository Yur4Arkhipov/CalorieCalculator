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
import com.jacqulin.calcalc.feature.home.ui.review.MealReviewScreen
import kotlinx.serialization.Serializable
import androidx.navigation.toRoute
import com.jacqulin.calcalc.core.domain.model.TempImage
import com.jacqulin.calcalc.feature.home.ui.mealdetail.MealDetailScreen

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

@Serializable
data class MealReviewRoute(
    val filePath: String
)

@Serializable
data class MealDetailRoute(
    val mealId: Int
)

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(route = HomeRoute, navOptions)

fun NavController.navigateToFavoriteChoose() = navigate(route = FavoriteMealChooseRoute)

fun NavController.navigateToMacroDetail() = navigate(route = MacroDetailRoute)

fun NavController.navigateToAiMealDescription() = navigate(route = AiMealDescriptionRoute)

fun NavController.navigateToManualAddMeal() = navigate(route = ManualAddMealRoute)

fun NavController.navigateToMealReview(temp: TempImage) = navigate(MealReviewRoute(temp.file.absolutePath))

fun NavController.navigateToMealDetail(mealId: Int) = navigate(MealDetailRoute(mealId))

fun NavGraphBuilder.homeSection(
    onNavigateToFavoriteChoose: () -> Unit,
    onNavigateToMacroDetail: () -> Unit,
    onNavigateToAiMealDescription: () -> Unit,
    onNavigateToManualAddMeal: () -> Unit,
    onNavigateToMealReview: (TempImage) -> Unit,
    onNavigateToMealDetail: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    navigation<HomeBaseRoute>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(
                onNavigateToAddFavoriteMeal = onNavigateToFavoriteChoose,
                onNavigateToMacroDetail = onNavigateToMacroDetail,
                onNavigateToAiMealDescription = onNavigateToAiMealDescription,
                onNavigateToManualAddMeal = onNavigateToManualAddMeal,
                onNavigateToMealReview = onNavigateToMealReview,
                onNavigateToMealDetail = onNavigateToMealDetail
            )
        }

        composable<FavoriteMealChooseRoute> {
            FavoriteMealChooseScreen(onBackClick = onBackClick)
        }

        composable<MacroDetailRoute> {
            MacroDetailScreen(
                onNavigateToMealDetail = onNavigateToMealDetail,
                onBackClick = onBackClick
            )
        }

        composable<AiMealDescriptionRoute> {
            AiMealDescriptionScreen(onBackClick = onBackClick)
        }

        composable<ManualAddMealRoute> {
            ManualAddMealScreen(onBackClick = onBackClick)
        }

        composable<MealReviewRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<MealReviewRoute>()

            MealReviewScreen(
                filePath = route.filePath,
                onBackClick = onBackClick
            )
        }

        composable<MealDetailRoute> {
            MealDetailScreen(
                onBackClick = onBackClick
            )
        }
    }
}
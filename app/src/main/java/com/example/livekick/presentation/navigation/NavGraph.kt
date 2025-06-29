package com.example.livekick.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.livekick.presentation.screen.favorites.FavoritesScreen
import com.example.livekick.presentation.screen.home.HomeScreen
import com.example.livekick.presentation.screen.match.MatchDetailScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onMatchClick = { matchId ->
                    navController.navigate(Screen.MatchDetail.createRoute(matchId))
                },
                onFavoritesClick = {
                    navController.navigate(Screen.Favorites.route)
                }
            )
        }
        
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onMatchClick = { matchId ->
                    navController.navigate(Screen.MatchDetail.createRoute(matchId))
                },
                onToggleFavorite = { matchId ->
                    // Обработка избранного будет в ViewModel
                }
            )
        }
        
        composable(
            route = Screen.MatchDetail.route,
            arguments = Screen.MatchDetail.arguments
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MatchDetailScreen(
                matchId = matchId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Favorites : Screen("favorites")
    object MatchDetail : Screen("match_detail/{matchId}") {
        fun createRoute(matchId: String) = "match_detail/$matchId"
        val arguments = listOf(
            navArgument("matchId") {
                type = NavType.StringType
            }
        )
    }
} 
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
    startDestination: String = "home"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToMatch = { matchId ->
                    navController.navigate("match/$matchId")
                },
                onNavigateToFavorites = {
                    navController.navigate("favorites")
                }
            )
        }
        
        composable("favorites") {
            FavoritesScreen(
                onNavigateToMatch = { matchId ->
                    navController.navigate("match/$matchId")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "match/{matchId}",
            arguments = listOf(
                navArgument("matchId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: return@composable
            MatchDetailScreen(
                matchId = matchId,
                onNavigateBack = {
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
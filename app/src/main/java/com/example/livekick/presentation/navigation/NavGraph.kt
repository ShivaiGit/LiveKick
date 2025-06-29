package com.example.livekick.presentation.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.livekick.presentation.screen.home.HomeScreen
import com.example.livekick.presentation.screen.match.MatchDetailScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    context: Context
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onMatchClick = { matchId ->
                    navController.navigate(Screen.MatchDetail.createRoute(matchId))
                },
                context = context
            )
        }
        
        composable(
            route = Screen.MatchDetail.route,
            arguments = listOf(
                navArgument("matchId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MatchDetailScreen(
                navController = navController,
                matchId = matchId,
                context = context
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MatchDetail : Screen("match_detail/{matchId}") {
        fun createRoute(matchId: String) = "match_detail/$matchId"
    }
} 
package com.example.livekick.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.livekick.presentation.screen.favorites.FavoritesScreen
import com.example.livekick.presentation.screen.home.HomeScreen
import com.example.livekick.presentation.screen.match.MatchDetailScreen
import com.example.livekick.presentation.screen.settings.NotificationSettingsScreen
import com.example.livekick.presentation.screen.statistics.TeamStatisticsScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = "home"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize
    ) {
        composable(
            route = "home",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300, easing = EaseInCubic)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            HomeScreen(
                onNavigateToMatch = { matchId ->
                    navController.navigate("match/$matchId")
                },
                onNavigateToFavorites = {
                    navController.navigate("favorites")
                },
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToStatistics = {
                    navController.navigate("statistics")
                }
            )
        }
        
        composable(
            route = "match/{matchId}",
            arguments = listOf(
                androidx.navigation.NavType.StringType
            ),
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(400, easing = EaseOutBack)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300, easing = EaseInCubic)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments?.getString("matchId") ?: ""
            MatchDetailScreen(
                matchId = matchId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "favorites",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300, easing = EaseInCubic)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
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
            route = "settings",
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300, easing = EaseInCubic)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            NotificationSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = "statistics",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300, easing = EaseInCubic)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            TeamStatisticsScreen(
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
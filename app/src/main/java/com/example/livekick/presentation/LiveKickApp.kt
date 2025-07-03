package com.example.livekick.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import com.example.livekick.data.repository.MatchRepositoryImpl
import com.example.livekick.presentation.navigation.NavGraph
import com.example.livekick.ui.theme.LiveKickTheme
import com.example.livekick.ui.theme.LocalThemeManager
import com.example.livekick.ui.theme.ThemeManager
import androidx.compose.material3.Text
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.Shadow
import androidx.compose.ui.unit.Offset

@Composable
fun LiveKickApp(matchRepository: MatchRepositoryImpl) {
    val themeManager = remember { ThemeManager() }
    val navController = rememberNavController()
    
    CompositionLocalProvider(LocalThemeManager provides themeManager) {
        val isDarkTheme = themeManager.isDarkTheme(isSystemInDarkTheme())
        
        LiveKickTheme(darkTheme = isDarkTheme) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Верхнее название
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp, bottom = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LiveKick",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                letterSpacing = 4.sp,
                                shadow = Shadow(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                    offset = Offset(2f, 4f),
                                    blurRadius = 6f
                                )
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                        )
                    }
                    // Контент с навигацией
                    Box(modifier = Modifier.weight(1f)) {
                        NavGraph(
                            navController = navController,
                            matchRepository = matchRepository,
                            startDestination = "home"
                        )
                    }
                    // Нижняя панель
                    BottomBar(navController = navController)
                }
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("home", Icons.Default.CalendarToday, "Все матчи"),
        BottomNavItem("live", Icons.Default.PlayArrow, "Live матчи"),
        BottomNavItem("favorites", Icons.Default.Favorite, "Избранное"),
        BottomNavItem("settings", Icons.Default.Settings, "Настройки")
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route || (item.route == "home" && currentRoute == null),
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                alwaysShowLabel = false
            )
        }
    }
}

data class BottomNavItem(val route: String, val icon: ImageVector, val label: String) 
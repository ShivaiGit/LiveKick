package com.example.livekick.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.livekick.data.repository.MatchRepositoryImpl
import com.example.livekick.presentation.navigation.NavGraph
import com.example.livekick.ui.theme.LiveKickTheme
import com.example.livekick.ui.theme.LocalThemeManager
import com.example.livekick.ui.theme.ThemeManager

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
                NavGraph(
                    navController = navController,
                    matchRepository = matchRepository,
                    startDestination = "home"
                )
            }
        }
    }
} 
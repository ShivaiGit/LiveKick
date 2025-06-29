package com.example.livekick.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

class ThemeManager {
    var themeMode by mutableStateOf(ThemeMode.SYSTEM)
        private set
    
    fun toggleTheme() {
        themeMode = when (themeMode) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
        }
    }
    
    fun isDarkTheme(isSystemInDarkTheme: Boolean): Boolean {
        return when (themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.SYSTEM -> isSystemInDarkTheme
        }
    }
}

val LocalThemeManager = staticCompositionLocalOf { ThemeManager() }

@Composable
fun rememberThemeManager(): ThemeManager {
    return LocalThemeManager.current
} 
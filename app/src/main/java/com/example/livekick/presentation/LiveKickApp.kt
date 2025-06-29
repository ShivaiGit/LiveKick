package com.example.livekick.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.livekick.presentation.navigation.NavGraph
import com.example.livekick.ui.theme.LiveKickTheme

@Composable
fun LiveKickApp(
    navController: NavHostController
) {
    LiveKickTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph(
                navController = navController
            )
        }
    }
} 
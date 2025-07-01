package com.example.livekick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable

class TestActivity : ComponentActivity() {
    companion object {
        var composable: (@Composable () -> Unit)? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            composable?.invoke()
        }
    }
} 
package com.example.livekick

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.livekick.data.repository.MatchRepositoryImpl
import com.example.livekick.notification.NotificationScheduler
import com.example.livekick.presentation.LiveKickApp
import com.example.livekick.ui.theme.LiveKickTheme
// import dagger.hilt.android.AndroidEntryPoint

// @AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    private lateinit var notificationScheduler: NotificationScheduler
    private lateinit var matchRepository: MatchRepositoryImpl
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Разрешение получено, запускаем уведомления
            notificationScheduler.startPeriodicNotifications()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Инициализируем репозиторий с контекстом
        matchRepository = MatchRepositoryImpl(this)
        
        notificationScheduler = NotificationScheduler(this)
        
        // Обрабатываем уведомления
        handleNotificationIntent(intent)
        
        // Запрашиваем разрешения для уведомлений
        requestNotificationPermissions()
        
        setContent {
            LiveKickTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LiveKickApp(matchRepository = matchRepository)
                }
            }
        }
    }
    
    private fun handleNotificationIntent(intent: Intent?) {
        intent?.let {
            val matchId = it.getStringExtra("matchId")
            if (matchId != null) {
                // Навигация к матчу будет обработана в NavGraph
                // Можно добавить логику для автоматической навигации
            }
        }
    }
    
    private fun requestNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Разрешение уже есть, запускаем уведомления
                    notificationScheduler.startPeriodicNotifications()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Показываем объяснение пользователю
                    // В реальном приложении здесь можно показать диалог
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    // Запрашиваем разрешение
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Для старых версий Android разрешение не требуется
            notificationScheduler.startPeriodicNotifications()
        }
    }
}
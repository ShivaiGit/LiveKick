package com.example.livekick.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.livekick.MainActivity
import com.example.livekick.R
import com.example.livekick.domain.model.Match
import com.example.livekick.domain.model.MatchStatus

class LiveKickNotificationManager(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID_MATCH_START = "match_start"
        const val CHANNEL_ID_MATCH_EVENTS = "match_events"
        const val CHANNEL_ID_GENERAL = "general"
        
        const val NOTIFICATION_ID_MATCH_START = 1001
        const val NOTIFICATION_ID_MATCH_EVENTS = 1002
        const val NOTIFICATION_ID_GENERAL = 1003
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_ID_MATCH_START,
                    "Начало матчей",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Уведомления о начале матчей"
                    enableVibration(true)
                    enableLights(true)
                },
                NotificationChannel(
                    CHANNEL_ID_MATCH_EVENTS,
                    "События матчей",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Уведомления о важных событиях в матчах"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_ID_GENERAL,
                    "Общие уведомления",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Общие уведомления приложения"
                }
            )
            
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
    
    fun showMatchStartNotification(match: Match) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("matchId", match.id)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MATCH_START)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Матч начинается!")
            .setContentText("${match.homeTeam.name} vs ${match.awayTeam.name}")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Матч ${match.homeTeam.name} vs ${match.awayTeam.name} начинается через 5 минут!\nЛига: ${match.league.name}"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_MATCH_START + match.id.hashCode(),
            notification
        )
    }
    
    fun showMatchEventNotification(match: Match, event: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("matchId", match.id)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MATCH_EVENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Событие в матче!")
            .setContentText("${match.homeTeam.name} ${match.homeScore} - ${match.awayScore} ${match.awayTeam.name}")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$event\n${match.homeTeam.name} ${match.homeScore} - ${match.awayScore} ${match.awayTeam.name}"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_MATCH_EVENTS + match.id.hashCode(),
            notification
        )
    }
    
    fun showGeneralNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_GENERAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        
        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_GENERAL,
            notification
        )
    }
    
    fun cancelMatchNotifications(matchId: String) {
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(NOTIFICATION_ID_MATCH_START + matchId.hashCode())
        notificationManager.cancel(NOTIFICATION_ID_MATCH_EVENTS + matchId.hashCode())
    }
    
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
} 
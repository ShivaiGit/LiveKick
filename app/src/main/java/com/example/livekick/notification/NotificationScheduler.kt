package com.example.livekick.notification

import android.content.Context
import androidx.work.*
import java.time.Duration
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {
    
    companion object {
        private const val MATCH_NOTIFICATION_WORK_NAME = "match_notification_work"
        private const val MATCH_NOTIFICATION_WORK_TAG = "match_notifications"
    }
    
    fun startPeriodicNotifications() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val periodicWorkRequest = PeriodicWorkRequestBuilder<MatchNotificationWorker>(
            15, TimeUnit.MINUTES, // Проверяем каждые 15 минут
            5, TimeUnit.MINUTES   // Гибкость 5 минут
        )
            .setConstraints(constraints)
            .addTag(MATCH_NOTIFICATION_WORK_TAG)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MATCH_NOTIFICATION_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicWorkRequest
        )
    }
    
    fun stopPeriodicNotifications() {
        WorkManager.getInstance(context).cancelUniqueWork(MATCH_NOTIFICATION_WORK_NAME)
        WorkManager.getInstance(context).cancelAllWorkByTag(MATCH_NOTIFICATION_WORK_TAG)
    }
    
    fun scheduleMatchStartNotification(matchId: String, matchTime: java.time.LocalDateTime) {
        val now = java.time.LocalDateTime.now()
        val delay = Duration.between(now, matchTime.minusMinutes(5)) // Уведомление за 5 минут
        
        if (delay.isNegative) {
            return // Матч уже начался или скоро начнется
        }
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<MatchNotificationWorker>()
            .setConstraints(constraints)
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .addTag("match_start_$matchId")
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            "match_start_$matchId",
            ExistingWorkPolicy.REPLACE,
            oneTimeWorkRequest
        )
    }
    
    fun cancelMatchStartNotification(matchId: String) {
        WorkManager.getInstance(context).cancelUniqueWork("match_start_$matchId")
    }
    
    fun isNotificationWorkScheduled(): Boolean {
        val workInfo = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(MATCH_NOTIFICATION_WORK_NAME)
            .get()
        
        return workInfo.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
    }
} 
package com.example.livekick.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.livekick.data.repository.MatchRepositoryImpl
import com.example.livekick.domain.model.MatchStatus
import com.example.livekick.domain.usecase.GetLiveMatchesUseCase
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MatchNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    private val matchRepository = MatchRepositoryImpl(context)
    private val getLiveMatchesUseCase = GetLiveMatchesUseCase(matchRepository)
    private val notificationManager = LiveKickNotificationManager(context)
    
    override suspend fun doWork(): Result {
        try {
            // Получаем все матчи
            val matches = getLiveMatchesUseCase()
            
            matches.collect { matchList ->
                val now = LocalDateTime.now()
                
                matchList.forEach { match ->
                    when (match.status) {
                        MatchStatus.SCHEDULED -> {
                            // Проверяем, начинается ли матч в ближайшие 5 минут
                            val timeUntilMatch = ChronoUnit.MINUTES.between(now, match.dateTime)
                            if (timeUntilMatch in 0..5) {
                                notificationManager.showMatchStartNotification(match)
                            }
                        }
                        
                        MatchStatus.LIVE -> {
                            // Проверяем важные события в живых матчах
                            checkImportantEvents(match)
                        }
                        
                        else -> {
                            // Матч завершен или отменен - отменяем уведомления
                            notificationManager.cancelMatchNotifications(match.id)
                        }
                    }
                }
            }
            
            return Result.success()
        } catch (e: Exception) {
            android.util.Log.e("LiveKick", "Ошибка фоновой работы уведомлений: ${e.message}", e)
            return Result.retry()
        }
    }
    
    private suspend fun checkImportantEvents(match: com.example.livekick.domain.model.Match) {
        // Проверяем события матча
        match.events.forEach { event ->
            when (event.type) {
                com.example.livekick.domain.model.EventType.GOAL -> {
                    notificationManager.showMatchEventNotification(
                        match,
                        "⚽ ГОЛ! ${event.player ?: "Игрок"} забил гол!"
                    )
                }
                com.example.livekick.domain.model.EventType.RED_CARD -> {
                    notificationManager.showMatchEventNotification(
                        match,
                        "🟥 КРАСНАЯ КАРТОЧКА! ${event.player ?: "Игрок"} удален!"
                    )
                }
                com.example.livekick.domain.model.EventType.YELLOW_CARD -> {
                    // Желтые карточки менее важны, можно не уведомлять
                }
                else -> {
                    // Другие события
                }
            }
        }
    }
} 
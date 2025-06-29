package com.example.livekick.data.local.repository

import com.example.livekick.data.local.AppDatabase
import com.example.livekick.data.local.mapper.LocalMapper
import com.example.livekick.domain.model.Match
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class LocalMatchRepository(
    private val database: AppDatabase
) {
    
    private val matchDao = database.matchDao()
    
    fun getLiveAndTodayMatches(): Flow<List<Match>> {
        val today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
        return matchDao.getLiveAndTodayMatches(today).map { entities ->
            LocalMapper.mapEntityListToMatches(entities)
        }
    }
    
    fun getFavoriteMatches(): Flow<List<Match>> {
        return matchDao.getFavoriteMatches().map { entities ->
            LocalMapper.mapEntityListToMatches(entities)
        }
    }
    
    suspend fun getMatchById(matchId: String): Match? {
        return matchDao.getMatchById(matchId)?.let { entity ->
            LocalMapper.mapEntityToMatch(entity)
        }
    }
    
    suspend fun saveMatches(matches: List<Match>) {
        val entities = LocalMapper.mapMatchesToEntities(matches)
        matchDao.insertMatches(entities)
    }
    
    suspend fun saveMatch(match: Match) {
        val entity = LocalMapper.mapMatchToEntity(match)
        matchDao.insertMatch(entity)
    }
    
    suspend fun updateFavoriteStatus(matchId: String, isFavorite: Boolean) {
        matchDao.updateFavoriteStatus(matchId, isFavorite)
    }
    
    suspend fun clearOldMatches() {
        // Удаляем матчи старше 7 дней
        val weekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        matchDao.deleteOldMatches(weekAgo)
    }
    
    suspend fun clearAllMatches() {
        matchDao.deleteAllMatches()
    }
} 
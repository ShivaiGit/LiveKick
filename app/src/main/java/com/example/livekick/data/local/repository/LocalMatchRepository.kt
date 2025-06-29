package com.example.livekick.data.local.repository

import com.example.livekick.data.local.AppDatabase
import com.example.livekick.data.local.entity.MatchEntity
import com.example.livekick.data.local.mapper.LocalMapper
import com.example.livekick.domain.model.Match
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalMatchRepository(private val database: AppDatabase) {
    
    private val matchDao = database.matchDao()
    
    fun getLiveAndTodayMatches(): Flow<List<Match>> {
        return matchDao.getLiveAndTodayMatches().map { entities ->
            entities.map { LocalMapper.mapEntityToMatch(it) }
        }
    }
    
    fun getFavoriteMatches(): Flow<List<Match>> {
        return matchDao.getFavoriteMatches().map { entities ->
            entities.map { LocalMapper.mapEntityToMatch(it) }
        }
    }
    
    suspend fun getMatchById(matchId: String): Match? {
        val entity = matchDao.getMatchById(matchId)
        return entity?.let { LocalMapper.mapEntityToMatch(it) }
    }
    
    suspend fun saveMatches(matches: List<Match>) {
        val entities = matches.map { LocalMapper.mapMatchToEntity(it) }
        matchDao.insertMatches(entities)
    }
    
    suspend fun updateFavoriteStatus(matchId: String, isFavorite: Boolean) {
        matchDao.updateFavoriteStatus(matchId, isFavorite)
    }
    
    suspend fun clearOldMatches() {
        val cutoffDate = LocalDateTime.now().minusDays(7).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        matchDao.deleteOldMatches(cutoffDate)
    }
    
    suspend fun clearAllMatches() {
        matchDao.clearAllMatches()
    }
} 
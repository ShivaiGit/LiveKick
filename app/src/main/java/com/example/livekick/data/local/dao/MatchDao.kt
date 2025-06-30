package com.example.livekick.data.local.dao

import androidx.room.*
import com.example.livekick.data.local.entity.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    
    @Query("SELECT * FROM matches ORDER BY dateTime ASC")
    fun getAllMatches(): Flow<List<MatchEntity>>
    
    @Query("SELECT * FROM matches WHERE status = 'LIVE' OR (dateTime >= datetime('now', 'start of day') AND status = 'SCHEDULED') ORDER BY dateTime ASC LIMIT 15")
    fun getLiveAndTodayMatches(): Flow<List<MatchEntity>>
    
    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: String): MatchEntity?
    
    @Query("SELECT * FROM matches WHERE isFavorite = 1 ORDER BY dateTime ASC")
    fun getFavoriteMatches(): Flow<List<MatchEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)
    
    @Update
    suspend fun updateMatch(match: MatchEntity)
    
    @Query("UPDATE matches SET isFavorite = :isFavorite WHERE id = :matchId")
    suspend fun updateFavoriteStatus(matchId: String, isFavorite: Boolean)
    
    @Query("DELETE FROM matches WHERE dateTime < :cutoffDate")
    suspend fun deleteOldMatches(cutoffDate: String)
    
    @Query("DELETE FROM matches")
    suspend fun clearAllMatches()
    
    @Query("SELECT id FROM matches WHERE isFavorite = 1")
    suspend fun getFavoriteMatchIds(): List<String>
} 
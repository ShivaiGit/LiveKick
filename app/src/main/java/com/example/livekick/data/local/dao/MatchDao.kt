package com.example.livekick.data.local.dao

import androidx.room.*
import com.example.livekick.data.local.entity.MatchEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface MatchDao {
    
    @Query("SELECT * FROM matches ORDER BY dateTime DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>
    
    @Query("SELECT * FROM matches WHERE isFavorite = 1 ORDER BY dateTime DESC")
    fun getFavoriteMatches(): Flow<List<MatchEntity>>
    
    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: String): MatchEntity?
    
    @Query("SELECT * FROM matches WHERE status IN ('LIVE', 'SCHEDULED') AND dateTime >= :today ORDER BY dateTime ASC LIMIT 10")
    fun getLiveAndTodayMatches(today: LocalDateTime): Flow<List<MatchEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)
    
    @Update
    suspend fun updateMatch(match: MatchEntity)
    
    @Query("UPDATE matches SET isFavorite = :isFavorite WHERE id = :matchId")
    suspend fun updateFavoriteStatus(matchId: String, isFavorite: Boolean)
    
    @Query("DELETE FROM matches WHERE lastUpdated < :timestamp")
    suspend fun deleteOldMatches(timestamp: Long)
    
    @Query("DELETE FROM matches")
    suspend fun deleteAllMatches()
} 
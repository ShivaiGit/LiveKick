package com.example.livekick.domain.repository

import com.example.livekick.domain.model.Match
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
    fun getLiveMatches(): Flow<List<Match>>
    fun getMatchesByLeague(leagueId: String): Flow<List<Match>>
    fun getMatchById(matchId: String): Flow<Match?>
    fun getFavoriteMatches(): Flow<List<Match>>
    suspend fun toggleFavorite(matchId: String)
    suspend fun refreshMatches()
    suspend fun clearAllMatches()
} 
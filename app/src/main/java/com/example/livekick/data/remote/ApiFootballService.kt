package com.example.livekick.data.remote

import com.example.livekick.data.remote.dto.ApiFootballResponse
import com.example.livekick.data.remote.dto.MatchStatisticsResponse
import com.example.livekick.data.remote.dto.MatchEventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiFootballService {
    
    // Live матчи
    @GET("matches-live")
    suspend fun getLiveMatches(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 50,
        @Query("lang") lang: String = "en"
    ): ApiFootballResponse
    
    // Матчи по дате
    @GET("matches-by-date")
    suspend fun getMatchesByDate(
        @Query("date") date: String, // формат YYYY-MM-DD
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 50,
        @Query("lang") lang: String = "en"
    ): ApiFootballResponse
    
    // Матч по ID
    @GET("matches")
    suspend fun getMatchById(
        @Query("id") matchId: String,
        @Query("lang") lang: String = "en"
    ): ApiFootballResponse
    
    // Матчи по лиге
    @GET("matches")
    suspend fun getMatchesByLeague(
        @Query("league_id") leagueId: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 50,
        @Query("lang") lang: String = "en"
    ): ApiFootballResponse
    
    // Лиги
    @GET("leagues")
    suspend fun getLeagues(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 50,
        @Query("lang") lang: String = "en"
    ): ApiFootballResponse
    
    // Страны
    @GET("countries")
    suspend fun getCountries(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 50,
        @Query("lang") lang: String = "en"
    ): ApiFootballResponse
    
    // Тестовый эндпоинт для проверки API
    @GET("leagues")
    suspend fun testApi(): ApiFootballResponse

    @GET("match-statistics")
    suspend fun getMatchStatistics(
        @Query("match_id") matchId: String,
        @Query("lang") lang: String = "en"
    ): List<MatchStatisticsResponse>

    @GET("match-events")
    suspend fun getMatchEvents(
        @Query("match_id") matchId: String,
        @Query("lang") lang: String = "en"
    ): List<MatchEventResponse>
} 
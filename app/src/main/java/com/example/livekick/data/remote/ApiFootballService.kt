package com.example.livekick.data.remote

import com.example.livekick.data.remote.dto.ApiFootballResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiFootballService {
    
    @GET("?APIkey=f81675edfc5e8e89f8a387158f1fc5dcdc34126b3b8deaa664d206019c0ba65d&action=get_live")
    suspend fun getLiveMatches(): ApiFootballResponse
    
    @GET("?APIkey=f81675edfc5e8e89f8a387158f1fc5dcdc34126b3b8deaa664d206019c0ba65d&action=get_events")
    suspend fun getMatchesByDate(
        @Query("from") dateFrom: String,
        @Query("to") dateTo: String
    ): ApiFootballResponse
    
    @GET("?APIkey=f81675edfc5e8e89f8a387158f1fc5dcdc34126b3b8deaa664d206019c0ba65d&action=get_events")
    suspend fun getMatchById(
        @Query("match_id") matchId: String
    ): ApiFootballResponse
    
    @GET("?APIkey=f81675edfc5e8e89f8a387158f1fc5dcdc34126b3b8deaa664d206019c0ba65d&action=get_events")
    suspend fun getMatchesByLeague(
        @Query("league_id") leagueId: String
    ): ApiFootballResponse
    
    @GET("?APIkey=f81675edfc5e8e89f8a387158f1fc5dcdc34126b3b8deaa664d206019c0ba65d&action=get_leagues")
    suspend fun getLeagues(): ApiFootballResponse
    
    // Тестовый эндпоинт для проверки API
    @GET("?APIkey=f81675edfc5e8e89f8a387158f1fc5dcdc34126b3b8deaa664d206019c0ba65d&action=get_countries")
    suspend fun testApi(): ApiFootballResponse
} 
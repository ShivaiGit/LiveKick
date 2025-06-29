package com.example.livekick.data.remote

import com.example.livekick.data.remote.dto.ApiFootballResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiFootballService {
    
    // Live матчи
    @GET("api/v1/lives")
    suspend fun getLiveMatches(): ApiFootballResponse
    
    // Матчи по дате
    @GET("api/v1/fixtures")
    suspend fun getMatchesByDate(
        @Query("date") date: String
    ): ApiFootballResponse
    
    // Матч по ID
    @GET("api/v1/fixtures/{id}")
    suspend fun getMatchById(
        @Query("id") matchId: String
    ): ApiFootballResponse
    
    // Матчи по лиге
    @GET("api/v1/fixtures")
    suspend fun getMatchesByLeague(
        @Query("league_id") leagueId: String
    ): ApiFootballResponse
    
    // Лиги
    @GET("api/v1/leagues")
    suspend fun getLeagues(): ApiFootballResponse
    
    // Страны
    @GET("api/v1/countries")
    suspend fun getCountries(): ApiFootballResponse
    
    // Тестовый эндпоинт для проверки API
    @GET("api/v1/countries")
    suspend fun testApi(): ApiFootballResponse
} 
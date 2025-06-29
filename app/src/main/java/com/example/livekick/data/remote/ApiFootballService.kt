package com.example.livekick.data.remote

import com.example.livekick.data.remote.dto.ApiFootballResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiFootballService {
    
    @Headers(
        "X-RapidAPI-Key: 0fa283c7950ab094c03f7cc0e5e28cf5",
        "X-RapidAPI-Host: api-football-v1.p.rapidapi.com"
    )
    @GET("fixtures")
    suspend fun getLiveMatches(
        @Query("live") live: String = "all"
    ): ApiFootballResponse
    
    @Headers(
        "X-RapidAPI-Key: 0fa283c7950ab094c03f7cc0e5e28cf5",
        "X-RapidAPI-Host: api-football-v1.p.rapidapi.com"
    )
    @GET("fixtures")
    suspend fun getMatchesByDate(
        @Query("date") date: String,
        @Query("league") leagueId: String? = null
    ): ApiFootballResponse
    
    @Headers(
        "X-RapidAPI-Key: 0fa283c7950ab094c03f7cc0e5e28cf5",
        "X-RapidAPI-Host: api-football-v1.p.rapidapi.com"
    )
    @GET("fixtures")
    suspend fun getMatchById(
        @Query("id") matchId: String
    ): ApiFootballResponse
    
    @Headers(
        "X-RapidAPI-Key: 0fa283c7950ab094c03f7cc0e5e28cf5",
        "X-RapidAPI-Host: api-football-v1.p.rapidapi.com"
    )
    @GET("fixtures")
    suspend fun getMatchesByLeague(
        @Query("league") leagueId: String,
        @Query("season") season: Int = 2024
    ): ApiFootballResponse
    
    @Headers(
        "X-RapidAPI-Key: 0fa283c7950ab094c03f7cc0e5e28cf5",
        "X-RapidAPI-Host: api-football-v1.p.rapidapi.com"
    )
    @GET("leagues")
    suspend fun getLeagues(
        @Query("country") country: String? = null
    ): ApiFootballResponse
} 
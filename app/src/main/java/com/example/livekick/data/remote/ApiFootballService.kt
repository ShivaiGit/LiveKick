package com.example.livekick.data.remote

import com.example.livekick.data.remote.dto.ApiFootballResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiFootballService {
    
    @Headers(
        "X-API-Key: 5z5lfbzk0zkeh88r"
    )
    @GET("matches/live")
    suspend fun getLiveMatches(): ApiFootballResponse
    
    @Headers(
        "X-API-Key: 5z5lfbzk0zkeh88r"
    )
    @GET("matches")
    suspend fun getMatchesByDate(
        @Query("date") date: String
    ): ApiFootballResponse
    
    @Headers(
        "X-API-Key: 5z5lfbzk0zkeh88r"
    )
    @GET("matches/{id}")
    suspend fun getMatchById(
        @Path("id") matchId: String
    ): ApiFootballResponse
    
    @Headers(
        "X-API-Key: 5z5lfbzk0zkeh88r"
    )
    @GET("leagues/{id}/matches")
    suspend fun getMatchesByLeague(
        @Path("id") leagueId: String
    ): ApiFootballResponse
    
    @Headers(
        "X-API-Key: 5z5lfbzk0zkeh88r"
    )
    @GET("leagues")
    suspend fun getLeagues(): ApiFootballResponse
} 
package com.example.livekick.data.remote

import com.example.livekick.data.remote.dto.ApiFootballResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiFootballService {
    
    @Headers(
        "X-Auth-Token: 981f8292b80243acb01c6b7f98bce050"
    )
    @GET("matches")
    suspend fun getLiveMatches(): ApiFootballResponse
    
    @Headers(
        "X-Auth-Token: 981f8292b80243acb01c6b7f98bce050"
    )
    @GET("matches")
    suspend fun getMatchesByDate(
        @Query("dateFrom") dateFrom: String,
        @Query("dateTo") dateTo: String
    ): ApiFootballResponse
    
    @Headers(
        "X-Auth-Token: 981f8292b80243acb01c6b7f98bce050"
    )
    @GET("matches/{id}")
    suspend fun getMatchById(
        @Path("id") matchId: String
    ): ApiFootballResponse
    
    @Headers(
        "X-Auth-Token: 981f8292b80243acb01c6b7f98bce050"
    )
    @GET("competitions/{id}/matches")
    suspend fun getMatchesByLeague(
        @Path("id") leagueId: String
    ): ApiFootballResponse
    
    @Headers(
        "X-Auth-Token: 981f8292b80243acb01c6b7f98bce050"
    )
    @GET("competitions")
    suspend fun getLeagues(): ApiFootballResponse
    
    // Альтернативные эндпоинты для тестирования
    @Headers(
        "X-API-Key: 5z5lfbzk0zkeh88r"
    )
    @GET("matches/live")
    suspend fun getLiveMatchesAlt(): ApiFootballResponse
    
    @Headers(
        "X-API-Key: 5z5lfbzk0zkeh88r"
    )
    @GET("matches")
    suspend fun getMatchesByDateAlt(
        @Query("date") date: String
    ): ApiFootballResponse
    
    // Простой тестовый эндпоинт
    @Headers(
        "X-API-Key: 5z5lfbzk0zkeh88r"
    )
    @GET("")
    suspend fun testApi(): ApiFootballResponse
} 
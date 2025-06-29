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
        @Query("dateTo") dateTo: String,
        @Query("competitions") competitions: String? = null
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
    suspend fun getMatchesByCompetition(
        @Path("id") competitionId: String,
        @Query("season") season: Int = 2024
    ): ApiFootballResponse
    
    @Headers(
        "X-Auth-Token: 981f8292b80243acb01c6b7f98bce050"
    )
    @GET("competitions")
    suspend fun getCompetitions(): ApiFootballResponse
} 
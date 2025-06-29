package com.example.livekick.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiFootballResponse(
    val success: Boolean,
    val data: List<MatchResponse>?,
    val message: String? = null,
    val error: String? = null
)

data class MatchResponse(
    val id: String,
    val league: LeagueDto,
    val homeTeam: TeamDto,
    val awayTeam: TeamDto,
    val homeScore: Int,
    val awayScore: Int,
    val status: String,
    val minute: Int?,
    val date: String,
    val time: String,
    val events: List<EventDto>? = emptyList()
)

data class LeagueDto(
    val id: String,
    val name: String,
    val country: String,
    val logo: String?
)

data class TeamDto(
    val id: String,
    val name: String,
    val shortName: String,
    val logo: String?
)

data class EventDto(
    val id: String,
    val type: String,
    val minute: Int,
    val team: TeamDto,
    val player: String?,
    val description: String?
) 
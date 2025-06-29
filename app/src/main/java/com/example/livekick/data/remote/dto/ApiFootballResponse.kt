package com.example.livekick.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiFootballResponse(
    val count: Int,
    val filters: Filters?,
    val matches: List<MatchResponse>?,
    val competitions: List<CompetitionResponse>?,
    val errorCode: Int? = null,
    val message: String? = null
)

data class Filters(
    val dateFrom: String?,
    val dateTo: String?,
    val permission: String?
)

data class MatchResponse(
    val id: Int,
    val competition: CompetitionDto,
    val season: SeasonDto,
    val utcDate: String,
    val status: String,
    val matchday: Int,
    val stage: String,
    val group: String?,
    val lastUpdated: String,
    val odds: OddsDto?,
    val score: ScoreDto,
    val homeTeam: TeamDto,
    val awayTeam: TeamDto,
    val referees: List<RefereeDto>?
)

data class CompetitionDto(
    val id: Int,
    val name: String,
    val code: String,
    val type: String,
    val emblem: String
)

data class SeasonDto(
    val id: Int,
    val startDate: String,
    val endDate: String,
    val currentMatchday: Int,
    val winner: String?
)

data class OddsDto(
    val msg: String
)

data class ScoreDto(
    val winner: String?,
    val duration: String,
    val fullTime: GoalsDto,
    val halfTime: GoalsDto
)

data class GoalsDto(
    val home: Int?,
    val away: Int?
)

data class TeamDto(
    val id: Int,
    val name: String,
    val shortName: String,
    val tla: String,
    val crest: String
)

data class RefereeDto(
    val id: Int,
    val name: String,
    val type: String,
    val nationality: String?
)

data class CompetitionResponse(
    val id: Int,
    val name: String,
    val code: String,
    val type: String,
    val emblem: String,
    val plan: String,
    val currentSeason: SeasonDto,
    val numberOfAvailableSeasons: Int,
    val lastUpdated: String
) 
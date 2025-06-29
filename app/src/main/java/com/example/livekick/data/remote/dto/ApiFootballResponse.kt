package com.example.livekick.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiFootballResponse(
    val get: String,
    val parameters: Map<String, Any>?,
    val errors: List<String>?,
    val results: Int,
    val paging: Paging?,
    val response: List<FixtureResponse>?
)

data class Paging(
    val current: Int,
    val total: Int
)

data class FixtureResponse(
    val fixture: FixtureDto,
    val league: LeagueDto,
    val teams: TeamsDto,
    val goals: GoalsDto,
    val score: ScoreDto,
    val events: List<EventDto>? = null
)

data class FixtureDto(
    val id: Int,
    val referee: String?,
    val timezone: String,
    val date: String,
    val timestamp: Long,
    val periods: PeriodsDto,
    val venue: VenueDto?,
    val status: StatusDto
)

data class PeriodsDto(
    val first: Long?,
    val second: Long?
)

data class VenueDto(
    val id: Int?,
    val name: String?,
    val city: String?
)

data class StatusDto(
    val long: String,
    val short: String,
    val elapsed: Int?
)

data class LeagueDto(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String?,
    val season: Int,
    val round: String
)

data class TeamsDto(
    val home: TeamDto,
    val away: TeamDto
)

data class TeamDto(
    val id: Int,
    val name: String,
    val logo: String
)

data class GoalsDto(
    val home: Int?,
    val away: Int?
)

data class ScoreDto(
    val halftime: GoalsDto,
    val fulltime: GoalsDto,
    val extratime: GoalsDto?,
    val penalty: GoalsDto?
)

data class EventDto(
    val time: TimeDto,
    val team: TeamDto,
    val player: PlayerDto,
    val assist: PlayerDto?,
    val type: String,
    val detail: String,
    val comments: String?
)

data class TimeDto(
    val elapsed: Int?,
    val extra: Int?
)

data class PlayerDto(
    val id: Int?,
    val name: String
) 
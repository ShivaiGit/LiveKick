package com.example.livekick.data.remote.dto

import com.google.gson.annotations.SerializedName

// Для прямых массивов матчей (API возвращает массив напрямую)
typealias ApiFootballResponse = List<MatchResponse>

data class MatchResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("tournament_id")
    val tournamentId: Int?,
    @SerializedName("tournament_name")
    val tournamentName: String?,
    @SerializedName("season_id")
    val seasonId: Int?,
    @SerializedName("season_name")
    val seasonName: String?,
    @SerializedName("status")
    val status: Status?,
    @SerializedName("status_type")
    val statusType: String?,
    @SerializedName("time")
    val time: String?,
    @SerializedName("home_team_id")
    val homeTeamId: Int?,
    @SerializedName("home_team_name")
    val homeTeamName: String?,
    @SerializedName("home_team_hash_image")
    val homeTeamLogo: String?,
    @SerializedName("home_team_score")
    val homeTeamScore: ScoreValue?,
    @SerializedName("away_team_id")
    val awayTeamId: Int?,
    @SerializedName("away_team_name")
    val awayTeamName: String?,
    @SerializedName("away_team_hash_image")
    val awayTeamLogo: String?,
    @SerializedName("away_team_score")
    val awayTeamScore: ScoreValue?,
    @SerializedName("start_time")
    val startTime: String?,
    @SerializedName("league_id")
    val leagueId: Int?,
    @SerializedName("league_name")
    val leagueName: String?,
    @SerializedName("league_hash_image")
    val leagueLogo: String?,
    @SerializedName("class_id")
    val classId: Int?,
    @SerializedName("class_name")
    val className: String?,
    @SerializedName("class_hash_image")
    val classLogo: String?
)

data class Periods(
    @SerializedName("first")
    val first: Int?,
    @SerializedName("second")
    val second: Int?
)

data class Venue(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("city")
    val city: String?
)

data class Status(
    @SerializedName("type")
    val type: String?,
    @SerializedName("reason")
    val reason: String?
)

data class LeagueResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("logo")
    val logo: String?,
    @SerializedName("flag")
    val flag: String?,
    @SerializedName("season")
    val season: Int,
    @SerializedName("round")
    val round: String?
)

data class CountryResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("code")
    val code: String?,
    @SerializedName("flag")
    val flag: String?
)

data class Teams(
    @SerializedName("home")
    val home: TeamResponse,
    @SerializedName("away")
    val away: TeamResponse
)

data class TeamResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("logo")
    val logo: String?
)

data class Goals(
    @SerializedName("home")
    val home: Int?,
    @SerializedName("away")
    val away: Int?
)

data class Score(
    @SerializedName("halftime")
    val halftime: ScoreDetail?,
    @SerializedName("fulltime")
    val fulltime: ScoreDetail?,
    @SerializedName("extratime")
    val extratime: ScoreDetail?,
    @SerializedName("penalty")
    val penalty: ScoreDetail?
)

data class ScoreDetail(
    @SerializedName("home")
    val home: Int?,
    @SerializedName("away")
    val away: Int?
)

data class ScoreValue(
    @SerializedName("current")
    val current: Int?,
    @SerializedName("display")
    val display: Int?,
    @SerializedName("period_1")
    val period1: Int?,
    @SerializedName("period_2")
    val period2: Int?,
    @SerializedName("default_time")
    val defaultTime: Int?
)

data class MatchStatisticsResponse(
    @SerializedName("team_id") val teamId: Int?,
    @SerializedName("team_name") val teamName: String?,
    @SerializedName("shots_on_target") val shotsOnTarget: Int?,
    @SerializedName("shots_off_target") val shotsOffTarget: Int?,
    @SerializedName("possession") val possession: Int?,
    @SerializedName("passes") val passes: Int?,
    @SerializedName("fouls") val fouls: Int?,
    @SerializedName("yellow_cards") val yellowCards: Int?,
    @SerializedName("red_cards") val redCards: Int?,
    @SerializedName("corners") val corners: Int?,
    @SerializedName("offsides") val offsides: Int?,
    @SerializedName("saves") val saves: Int?
)

data class MatchEventResponse(
    @SerializedName("id") val id: Int?,
    @SerializedName("type") val type: String?, // goal, yellow_card, red_card, substitution, etc.
    @SerializedName("minute") val minute: Int?,
    @SerializedName("team_id") val teamId: Int?,
    @SerializedName("team_name") val teamName: String?,
    @SerializedName("player_name") val playerName: String?,
    @SerializedName("assist_name") val assistName: String?,
    @SerializedName("description") val description: String?
) 
package com.example.livekick.data.remote.dto

import com.google.gson.annotations.SerializedName

// Для прямых массивов матчей (API возвращает массив напрямую)
typealias ApiFootballResponse = List<MatchResponse>

data class MatchResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("referee")
    val referee: String?,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("timestamp")
    val timestamp: Int,
    @SerializedName("periods")
    val periods: Periods?,
    @SerializedName("venue")
    val venue: Venue?,
    @SerializedName("status")
    val status: Status,
    @SerializedName("league")
    val league: LeagueResponse,
    @SerializedName("country")
    val country: CountryResponse,
    @SerializedName("teams")
    val teams: Teams,
    @SerializedName("goals")
    val goals: Goals,
    @SerializedName("score")
    val score: Score
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
    @SerializedName("long")
    val long: String,
    @SerializedName("short")
    val short: String,
    @SerializedName("elapsed")
    val elapsed: Int?
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
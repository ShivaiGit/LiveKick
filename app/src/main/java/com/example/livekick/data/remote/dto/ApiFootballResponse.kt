package com.example.livekick.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ApiFootballResponse(
    val result: List<MatchResponse>? = null,
    val error: Int? = null,
    val message: String? = null
)

data class MatchResponse(
    @SerializedName("match_id")
    val matchId: String,
    @SerializedName("country_id")
    val countryId: String,
    @SerializedName("country_name")
    val countryName: String,
    @SerializedName("league_id")
    val leagueId: String,
    @SerializedName("league_name")
    val leagueName: String,
    @SerializedName("match_date")
    val matchDate: String,
    @SerializedName("match_status")
    val matchStatus: String,
    @SerializedName("match_time")
    val matchTime: String,
    @SerializedName("match_hometeam_id")
    val homeTeamId: String,
    @SerializedName("match_hometeam_name")
    val homeTeamName: String,
    @SerializedName("match_hometeam_score")
    val homeTeamScore: String,
    @SerializedName("match_awayteam_id")
    val awayTeamId: String,
    @SerializedName("match_awayteam_name")
    val awayTeamName: String,
    @SerializedName("match_awayteam_score")
    val awayTeamScore: String,
    @SerializedName("match_hometeam_halftime_score")
    val homeTeamHalftimeScore: String,
    @SerializedName("match_awayteam_halftime_score")
    val awayTeamHalftimeScore: String,
    @SerializedName("match_hometeam_extra_score")
    val homeTeamExtraScore: String?,
    @SerializedName("match_awayteam_extra_score")
    val awayTeamExtraScore: String?,
    @SerializedName("match_hometeam_penalty_score")
    val homeTeamPenaltyScore: String?,
    @SerializedName("match_awayteam_penalty_score")
    val awayTeamPenaltyScore: String?,
    @SerializedName("match_hometeam_ft_score")
    val homeTeamFtScore: String?,
    @SerializedName("match_awayteam_ft_score")
    val awayTeamFtScore: String?,
    @SerializedName("match_hometeam_system")
    val homeTeamSystem: String?,
    @SerializedName("match_awayteam_system")
    val awayTeamSystem: String?,
    @SerializedName("match_live")
    val matchLive: String,
    @SerializedName("match_round")
    val matchRound: String?,
    @SerializedName("match_stadium")
    val matchStadium: String?,
    @SerializedName("match_referee")
    val matchReferee: String?,
    @SerializedName("team_home_badge")
    val homeTeamBadge: String?,
    @SerializedName("team_away_badge")
    val awayTeamBadge: String?,
    @SerializedName("league_logo")
    val leagueLogo: String?,
    @SerializedName("country_logo")
    val countryLogo: String?,
    @SerializedName("league_year")
    val leagueYear: String?,
    @SerializedName("fko")
    val fko: String?,
    @SerializedName("match_ht_score")
    val matchHtScore: String?,
    @SerializedName("match_ft_score")
    val matchFtScore: String?,
    @SerializedName("match_et_score")
    val matchEtScore: String?,
    @SerializedName("match_ps_score")
    val matchPsScore: String?
)

// Для ответов с лигами
data class LeagueResponse(
    val result: List<LeagueData>? = null,
    val error: Int? = null,
    val message: String? = null
)

data class LeagueData(
    @SerializedName("league_id")
    val leagueId: String,
    @SerializedName("league_name")
    val leagueName: String,
    @SerializedName("country_id")
    val countryId: String,
    @SerializedName("country_name")
    val countryName: String,
    @SerializedName("league_logo")
    val leagueLogo: String?,
    @SerializedName("country_logo")
    val countryLogo: String?
)

// Для ответов со странами
data class CountryResponse(
    val result: List<CountryData>? = null,
    val error: Int? = null,
    val message: String? = null
)

data class CountryData(
    @SerializedName("country_id")
    val countryId: String,
    @SerializedName("country_name")
    val countryName: String,
    @SerializedName("country_logo")
    val countryLogo: String?
) 
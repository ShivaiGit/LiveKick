package com.example.livekick.domain.model

data class MatchStatistics(
    val matchId: String,
    val homeTeamStats: TeamStatistics,
    val awayTeamStats: TeamStatistics,
    val possession: PossessionStats,
    val shots: ShotStats,
    val cards: CardStats,
    val corners: CornerStats,
    val fouls: FoulStats,
    val offsides: OffsideStats,
    val saves: SaveStats,
    val passes: PassStats
)

data class TeamStatistics(
    val teamId: String,
    val teamName: String,
    val formation: String?,
    val goals: Int,
    val shots: Int,
    val shotsOnTarget: Int,
    val possession: Int, // percentage
    val passes: Int,
    val passAccuracy: Int, // percentage
    val fouls: Int,
    val yellowCards: Int,
    val redCards: Int,
    val offsides: Int,
    val corners: Int,
    val saves: Int
)

data class PossessionStats(
    val homePossession: Int, // percentage
    val awayPossession: Int // percentage
)

data class ShotStats(
    val homeShots: Int,
    val awayShots: Int,
    val homeShotsOnTarget: Int,
    val awayShotsOnTarget: Int,
    val homeShotsOffTarget: Int,
    val awayShotsOffTarget: Int,
    val homeBlockedShots: Int,
    val awayBlockedShots: Int
)

data class CardStats(
    val homeYellowCards: Int,
    val awayYellowCards: Int,
    val homeRedCards: Int,
    val awayRedCards: Int
)

data class CornerStats(
    val homeCorners: Int,
    val awayCorners: Int
)

data class FoulStats(
    val homeFouls: Int,
    val awayFouls: Int
)

data class OffsideStats(
    val homeOffsides: Int,
    val awayOffsides: Int
)

data class SaveStats(
    val homeSaves: Int,
    val awaySaves: Int
)

data class PassStats(
    val homePasses: Int,
    val awayPasses: Int,
    val homePassAccuracy: Int, // percentage
    val awayPassAccuracy: Int // percentage
) 
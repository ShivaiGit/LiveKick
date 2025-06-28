package com.example.livekick.domain.model

import java.time.LocalDateTime

data class Match(
    val id: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val homeScore: Int,
    val awayScore: Int,
    val status: MatchStatus,
    val minute: Int?,
    val league: League,
    val dateTime: LocalDateTime,
    val events: List<MatchEvent> = emptyList(),
    val isFavorite: Boolean = false
)

data class Team(
    val id: String,
    val name: String,
    val shortName: String,
    val logoUrl: String? = null
)

data class League(
    val id: String,
    val name: String,
    val country: String,
    val logoUrl: String? = null
)

enum class MatchStatus {
    SCHEDULED,
    LIVE,
    FINISHED,
    POSTPONED,
    CANCELLED
}

data class MatchEvent(
    val id: String,
    val type: EventType,
    val minute: Int,
    val team: Team,
    val player: String? = null,
    val description: String? = null
)

enum class EventType {
    GOAL,
    YELLOW_CARD,
    RED_CARD,
    SUBSTITUTION,
    CORNER,
    FOUL
} 
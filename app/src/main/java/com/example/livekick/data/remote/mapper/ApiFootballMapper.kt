package com.example.livekick.data.remote.mapper

import com.example.livekick.data.remote.dto.*
import com.example.livekick.domain.model.*
import java.time.LocalDateTime
import java.time.ZoneOffset

object ApiFootballMapper {
    
    fun mapFixtureResponseToMatch(fixtureResponse: FixtureResponse): Match {
        return Match(
            id = fixtureResponse.fixture.id.toString(),
            homeTeam = mapTeamDtoToTeam(fixtureResponse.teams.home),
            awayTeam = mapTeamDtoToTeam(fixtureResponse.teams.away),
            homeScore = fixtureResponse.goals.home ?: 0,
            awayScore = fixtureResponse.goals.away ?: 0,
            status = mapStatusToMatchStatus(fixtureResponse.fixture.status),
            minute = fixtureResponse.fixture.status.elapsed,
            league = mapLeagueDtoToLeague(fixtureResponse.league),
            dateTime = LocalDateTime.ofEpochSecond(fixtureResponse.fixture.timestamp, 0, ZoneOffset.UTC),
            events = fixtureResponse.events?.map { mapEventDtoToEvent(it) } ?: emptyList(),
            isFavorite = false // Будет управляться локально
        )
    }
    
    private fun mapTeamDtoToTeam(teamDto: TeamDto): Team {
        return Team(
            id = teamDto.id.toString(),
            name = teamDto.name,
            shortName = teamDto.name.split(" ").take(2).joinToString(" "),
            logoUrl = teamDto.logo
        )
    }
    
    private fun mapLeagueDtoToLeague(leagueDto: LeagueDto): League {
        return League(
            id = leagueDto.id.toString(),
            name = leagueDto.name,
            country = leagueDto.country,
            logoUrl = leagueDto.logo
        )
    }
    
    private fun mapStatusToMatchStatus(statusDto: StatusDto): MatchStatus {
        return when (statusDto.short.lowercase()) {
            "1h", "2h", "ht", "et", "p", "bt" -> MatchStatus.LIVE
            "ft", "aet", "pen", "pft" -> MatchStatus.FINISHED
            "ns", "tbd" -> MatchStatus.SCHEDULED
            "canc", "abnd", "susp", "int", "pst" -> MatchStatus.CANCELLED
            else -> MatchStatus.SCHEDULED
        }
    }
    
    private fun mapEventDtoToEvent(eventDto: EventDto): MatchEvent {
        return MatchEvent(
            id = "${eventDto.time.elapsed}_${eventDto.player.id}",
            type = mapEventType(eventDto.type, eventDto.detail),
            minute = eventDto.time.elapsed ?: 0,
            team = mapTeamDtoToTeam(eventDto.team),
            player = eventDto.player.name,
            description = eventDto.detail
        )
    }
    
    private fun mapEventType(type: String, detail: String): EventType {
        return when {
            type.lowercase() == "goal" -> EventType.GOAL
            detail.lowercase().contains("yellow") -> EventType.YELLOW_CARD
            detail.lowercase().contains("red") -> EventType.RED_CARD
            detail.lowercase().contains("substitution") -> EventType.SUBSTITUTION
            detail.lowercase().contains("corner") -> EventType.CORNER
            else -> EventType.FOUL
        }
    }
    
    fun mapFixtureResponseListToMatches(fixtures: List<FixtureResponse>): List<Match> {
        return fixtures.map { mapFixtureResponseToMatch(it) }
    }
} 
package com.example.livekick.data.remote.mapper

import com.example.livekick.data.remote.dto.*
import com.example.livekick.domain.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ApiFootballMapper {
    
    fun mapMatchResponseToMatch(matchResponse: MatchResponse): Match {
        return Match(
            id = matchResponse.id,
            homeTeam = mapTeamDtoToTeam(matchResponse.homeTeam),
            awayTeam = mapTeamDtoToTeam(matchResponse.awayTeam),
            homeScore = matchResponse.homeScore,
            awayScore = matchResponse.awayScore,
            status = mapStatusToMatchStatus(matchResponse.status),
            minute = matchResponse.minute,
            league = mapLeagueDtoToLeague(matchResponse.league),
            dateTime = parseDateTime(matchResponse.date, matchResponse.time),
            events = matchResponse.events?.map { mapEventDtoToEvent(it) } ?: emptyList(),
            isFavorite = false // Будет управляться локально
        )
    }
    
    private fun mapTeamDtoToTeam(teamDto: TeamDto): Team {
        return Team(
            id = teamDto.id,
            name = teamDto.name,
            shortName = teamDto.shortName,
            logoUrl = teamDto.logo
        )
    }
    
    private fun mapLeagueDtoToLeague(leagueDto: LeagueDto): League {
        return League(
            id = leagueDto.id,
            name = leagueDto.name,
            country = leagueDto.country,
            logoUrl = leagueDto.logo
        )
    }
    
    private fun mapEventDtoToEvent(eventDto: EventDto): MatchEvent {
        return MatchEvent(
            id = eventDto.id,
            type = mapEventType(eventDto.type),
            minute = eventDto.minute,
            team = mapTeamDtoToTeam(eventDto.team),
            player = eventDto.player,
            description = eventDto.description
        )
    }
    
    private fun mapEventType(type: String): EventType {
        return when (type.lowercase()) {
            "goal" -> EventType.GOAL
            "yellow_card" -> EventType.YELLOW_CARD
            "red_card" -> EventType.RED_CARD
            "substitution" -> EventType.SUBSTITUTION
            "corner" -> EventType.CORNER
            "foul" -> EventType.FOUL
            else -> EventType.GOAL
        }
    }
    
    private fun mapStatusToMatchStatus(status: String): MatchStatus {
        return when (status.lowercase()) {
            "live", "in_play", "playing" -> MatchStatus.LIVE
            "finished", "completed" -> MatchStatus.FINISHED
            "scheduled", "not_started" -> MatchStatus.SCHEDULED
            "cancelled", "postponed" -> MatchStatus.CANCELLED
            else -> MatchStatus.SCHEDULED
        }
    }
    
    private fun parseDateTime(date: String, time: String): LocalDateTime {
        val dateTimeString = "${date}T${time}"
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        return LocalDateTime.parse(dateTimeString, formatter)
    }
    
    fun mapMatchResponseListToMatches(matches: List<MatchResponse>): List<Match> {
        return matches.map { mapMatchResponseToMatch(it) }
    }
} 
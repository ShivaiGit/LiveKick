package com.example.livekick.data.remote.mapper

import com.example.livekick.data.remote.dto.*
import com.example.livekick.domain.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ApiFootballMapper {
    
    fun mapMatchResponseToMatch(matchResponse: MatchResponse): Match {
        return Match(
            id = matchResponse.id.toString(),
            homeTeam = mapTeamDtoToTeam(matchResponse.homeTeam),
            awayTeam = mapTeamDtoToTeam(matchResponse.awayTeam),
            homeScore = matchResponse.score.fullTime.home ?: 0,
            awayScore = matchResponse.score.fullTime.away ?: 0,
            status = mapStatusToMatchStatus(matchResponse.status),
            minute = null, // API не предоставляет минуту матча
            league = mapCompetitionDtoToLeague(matchResponse.competition),
            dateTime = parseDateTime(matchResponse.utcDate),
            events = emptyList(), // API не предоставляет события матча в бесплатной версии
            isFavorite = false // Будет управляться локально
        )
    }
    
    private fun mapTeamDtoToTeam(teamDto: TeamDto): Team {
        return Team(
            id = teamDto.id.toString(),
            name = teamDto.name,
            shortName = teamDto.shortName,
            logoUrl = teamDto.crest
        )
    }
    
    private fun mapCompetitionDtoToLeague(competitionDto: CompetitionDto): League {
        return League(
            id = competitionDto.id.toString(),
            name = competitionDto.name,
            country = "", // API не предоставляет страну в этом эндпоинте
            logoUrl = competitionDto.emblem
        )
    }
    
    private fun mapStatusToMatchStatus(status: String): MatchStatus {
        return when (status.lowercase()) {
            "live", "in_play" -> MatchStatus.LIVE
            "finished", "postponed", "suspended" -> MatchStatus.FINISHED
            "scheduled", "timed" -> MatchStatus.SCHEDULED
            "cancelled" -> MatchStatus.CANCELLED
            else -> MatchStatus.SCHEDULED
        }
    }
    
    private fun parseDateTime(dateTimeString: String): LocalDateTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return LocalDateTime.parse(dateTimeString, formatter)
    }
    
    fun mapMatchResponseListToMatches(matches: List<MatchResponse>): List<Match> {
        return matches.map { mapMatchResponseToMatch(it) }
    }
} 
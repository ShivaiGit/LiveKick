package com.example.livekick.data.remote.mapper

import com.example.livekick.data.remote.dto.*
import com.example.livekick.domain.model.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ApiFootballMapper {
    
    fun mapMatchResponseToMatch(matchResponse: MatchResponse): Match {
        return Match(
            id = matchResponse.matchId,
            homeTeam = mapHomeTeam(matchResponse),
            awayTeam = mapAwayTeam(matchResponse),
            homeScore = matchResponse.homeTeamScore.toIntOrNull() ?: 0,
            awayScore = matchResponse.awayTeamScore.toIntOrNull() ?: 0,
            status = mapStatusToMatchStatus(matchResponse.matchStatus, matchResponse.matchLive),
            minute = extractMinuteFromStatus(matchResponse.matchStatus),
            league = mapLeague(matchResponse),
            dateTime = parseDateTime(matchResponse.matchDate, matchResponse.matchTime),
            events = emptyList(), // API не предоставляет события матча в бесплатной версии
            isFavorite = false // Будет управляться локально
        )
    }
    
    private fun mapHomeTeam(matchResponse: MatchResponse): Team {
        return Team(
            id = matchResponse.homeTeamId,
            name = matchResponse.homeTeamName,
            shortName = extractShortName(matchResponse.homeTeamName),
            logoUrl = matchResponse.homeTeamBadge ?: ""
        )
    }
    
    private fun mapAwayTeam(matchResponse: MatchResponse): Team {
        return Team(
            id = matchResponse.awayTeamId,
            name = matchResponse.awayTeamName,
            shortName = extractShortName(matchResponse.awayTeamName),
            logoUrl = matchResponse.awayTeamBadge ?: ""
        )
    }
    
    private fun mapLeague(matchResponse: MatchResponse): League {
        return League(
            id = matchResponse.leagueId,
            name = matchResponse.leagueName,
            country = matchResponse.countryName,
            logoUrl = matchResponse.leagueLogo ?: ""
        )
    }
    
    private fun mapStatusToMatchStatus(status: String, live: String): MatchStatus {
        return when {
            live == "1" -> MatchStatus.LIVE
            status.contains("Finished", ignoreCase = true) -> MatchStatus.FINISHED
            status.contains("Postponed", ignoreCase = true) -> MatchStatus.CANCELLED
            status.contains("Cancelled", ignoreCase = true) -> MatchStatus.CANCELLED
            else -> MatchStatus.SCHEDULED
        }
    }
    
    private fun extractMinuteFromStatus(status: String): Int? {
        // Попытка извлечь минуту из статуса (например, "1st Half 23'")
        val minuteRegex = Regex("(\\d+)'")
        val match = minuteRegex.find(status)
        return match?.groupValues?.get(1)?.toIntOrNull()
    }
    
    private fun extractShortName(fullName: String): String {
        // Извлекаем короткое название команды
        return when {
            fullName.contains("Manchester City") -> "MCI"
            fullName.contains("Arsenal") -> "ARS"
            fullName.contains("Liverpool") -> "LIV"
            fullName.contains("Real Madrid") -> "RMA"
            fullName.contains("Barcelona") -> "BAR"
            fullName.contains("Bayern Munich") -> "BAY"
            fullName.contains("PSG") -> "PSG"
            fullName.contains("Juventus") -> "JUV"
            fullName.contains("AC Milan") -> "MIL"
            fullName.contains("Inter") -> "INT"
            fullName.contains("Chelsea") -> "CHE"
            fullName.contains("Manchester United") -> "MUN"
            else -> fullName.take(3).uppercase()
        }
    }
    
    private fun parseDateTime(date: String, time: String): LocalDateTime {
        try {
            // Формат даты: "2024-01-15", время: "20:30"
            val dateTimeString = "${date}T${time}:00"
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            return LocalDateTime.parse(dateTimeString, formatter)
        } catch (e: Exception) {
            // Если парсинг не удался, возвращаем текущее время
            return LocalDateTime.now()
        }
    }
    
    fun mapMatchResponseListToMatches(matches: List<MatchResponse>): List<Match> {
        return matches.map { mapMatchResponseToMatch(it) }
    }
} 
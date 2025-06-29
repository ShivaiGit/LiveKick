package com.example.livekick.data.remote.mapper

import android.util.Log
import com.example.livekick.data.remote.dto.*
import com.example.livekick.domain.model.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object ApiFootballMapper {
    
    fun mapMatchResponseToMatch(matchResponse: MatchResponse): Match {
        try {
            Log.d("LiveKick", "Маппинг матча: ${matchResponse.id}")
            Log.d("LiveKick", "Домашняя команда: ${matchResponse.teams.home.name}")
            Log.d("LiveKick", "Гостевая команда: ${matchResponse.teams.away.name}")
            Log.d("LiveKick", "Счет: ${matchResponse.goals.home} - ${matchResponse.goals.away}")
            Log.d("LiveKick", "Статус: ${matchResponse.status.long}")
            Log.d("LiveKick", "Elapsed: ${matchResponse.status.elapsed}")
            
            return Match(
                id = matchResponse.id.toString(),
                homeTeam = mapHomeTeam(matchResponse),
                awayTeam = mapAwayTeam(matchResponse),
                homeScore = matchResponse.goals.home ?: 0,
                awayScore = matchResponse.goals.away ?: 0,
                status = mapStatusToMatchStatus(matchResponse.status),
                minute = matchResponse.status.elapsed,
                league = mapLeague(matchResponse),
                dateTime = parseDateTime(matchResponse.date),
                events = emptyList(), // API не предоставляет события матча в бесплатной версии
                isFavorite = false // Будет управляться локально
            )
        } catch (e: Exception) {
            Log.e("LiveKick", "Ошибка маппинга матча ${matchResponse.id}: ${e.message}", e)
            throw e
        }
    }
    
    private fun mapHomeTeam(matchResponse: MatchResponse): Team {
        return Team(
            id = matchResponse.teams.home.id.toString(),
            name = matchResponse.teams.home.name,
            shortName = extractShortName(matchResponse.teams.home.name),
            logoUrl = matchResponse.teams.home.logo
        )
    }
    
    private fun mapAwayTeam(matchResponse: MatchResponse): Team {
        return Team(
            id = matchResponse.teams.away.id.toString(),
            name = matchResponse.teams.away.name,
            shortName = extractShortName(matchResponse.teams.away.name),
            logoUrl = matchResponse.teams.away.logo
        )
    }
    
    private fun mapLeague(matchResponse: MatchResponse): League {
        return League(
            id = matchResponse.league.id.toString(),
            name = matchResponse.league.name,
            country = matchResponse.country.name,
            logoUrl = matchResponse.league.logo
        )
    }
    
    private fun mapStatusToMatchStatus(status: Status): MatchStatus {
        return when {
            status.short == "LIVE" || status.short == "HT" || status.short == "2H" -> MatchStatus.LIVE
            status.short == "FT" || status.short == "AET" || status.short == "PEN" -> MatchStatus.FINISHED
            status.short == "PST" -> MatchStatus.POSTPONED
            status.short == "CANC" -> MatchStatus.CANCELLED
            else -> MatchStatus.SCHEDULED
        }
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
    
    private fun parseDateTime(dateString: String): LocalDateTime {
        try {
            // Формат даты от SportDevs: "2024-01-15T20:30:00+00:00"
            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            return LocalDateTime.parse(dateString, formatter)
        } catch (e: Exception) {
            try {
                // Альтернативный формат без timezone
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                return LocalDateTime.parse(dateString, formatter)
            } catch (e2: Exception) {
                // Если парсинг не удался, возвращаем текущее время
                Log.e("LiveKick", "Ошибка парсинга даты: $dateString", e2)
                return LocalDateTime.now()
            }
        }
    }
    
    fun mapMatchResponseListToMatches(matches: List<MatchResponse>): List<Match> {
        return matches.map { mapMatchResponseToMatch(it) }
    }
} 
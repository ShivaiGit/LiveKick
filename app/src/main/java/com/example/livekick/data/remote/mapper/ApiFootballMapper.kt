package com.example.livekick.data.remote.mapper

import android.util.Log
import com.example.livekick.data.remote.dto.*
import com.example.livekick.domain.model.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object ApiFootballMapper {
    
    fun mapMatchResponseToMatch(matchResponse: MatchResponse): Match? {
        try {
            // Проверяем, что есть минимально необходимые данные
            val homeId = matchResponse.homeTeamId
            val homeName = matchResponse.homeTeamName
            val awayId = matchResponse.awayTeamId
            val awayName = matchResponse.awayTeamName
            val homeScore = matchResponse.homeTeamScore?.current
            val awayScore = matchResponse.awayTeamScore?.current
            val leagueId = matchResponse.leagueId
            val leagueName = matchResponse.leagueName
            val status = matchResponse.status
            val statusType = matchResponse.statusType
            val startTime = matchResponse.startTime
            if (homeId == null || homeName == null || awayId == null || awayName == null || leagueId == null || leagueName == null || status == null || startTime == null) {
                Log.w("LiveKick", "Match ${matchResponse.id} пропущен из-за отсутствия обязательных данных")
                return null
            }
            return Match(
                id = matchResponse.id.toString(),
                homeTeam = Team(
                    id = homeId.toString(),
                    name = homeName,
                    shortName = extractShortName(homeName),
                    logoUrl = matchResponse.homeTeamLogo
                ),
                awayTeam = Team(
                    id = awayId.toString(),
                    name = awayName,
                    shortName = extractShortName(awayName),
                    logoUrl = matchResponse.awayTeamLogo
                ),
                homeScore = homeScore ?: 0,
                awayScore = awayScore ?: 0,
                status = mapStatusToMatchStatus(status, statusType),
                minute = parseMinute(matchResponse.time),
                league = League(
                    id = leagueId.toString(),
                    name = leagueName,
                    country = matchResponse.className ?: "",
                    logoUrl = matchResponse.leagueLogo
                ),
                dateTime = parseDateTime(startTime),
                events = emptyList(),
                isFavorite = false
            )
        } catch (e: Exception) {
            Log.e("LiveKick", "Ошибка маппинга матча ${matchResponse.id}: ${e.message}", e)
            return null
        }
    }
    
    private fun mapStatusToMatchStatus(status: Status?, statusType: String?): MatchStatus {
        // Пример: status.type = "live", statusType = "live", status.reason = "1st half"
        return when {
            statusType?.equals("live", ignoreCase = true) == true || status?.type?.equals("live", ignoreCase = true) == true -> MatchStatus.LIVE
            statusType?.equals("finished", ignoreCase = true) == true || status?.type?.equals("finished", ignoreCase = true) == true -> MatchStatus.FINISHED
            statusType?.equals("postponed", ignoreCase = true) == true || status?.type?.equals("postponed", ignoreCase = true) == true -> MatchStatus.POSTPONED
            statusType?.equals("cancelled", ignoreCase = true) == true || status?.type?.equals("cancelled", ignoreCase = true) == true -> MatchStatus.CANCELLED
            else -> MatchStatus.SCHEDULED
        }
    }
    
    private fun parseMinute(time: String?): Int? {
        // Пример: "69'", "HT", "01'", "45+1'"
        if (time == null) return null
        val regex = Regex("(\\d+)")
        val match = regex.find(time)
        return match?.value?.toIntOrNull()
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
        return matches.mapNotNull { mapMatchResponseToMatch(it) }
    }
} 
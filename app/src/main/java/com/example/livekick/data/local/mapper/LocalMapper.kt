package com.example.livekick.data.local.mapper

import com.example.livekick.data.local.entity.MatchEntity
import com.example.livekick.domain.model.*

object LocalMapper {
    
    fun mapMatchToEntity(match: Match): MatchEntity {
        return MatchEntity(
            id = match.id,
            homeTeamId = match.homeTeam.id,
            homeTeamName = match.homeTeam.name,
            homeTeamShortName = match.homeTeam.shortName,
            homeTeamLogoUrl = match.homeTeam.logoUrl ?: "",
            awayTeamId = match.awayTeam.id,
            awayTeamName = match.awayTeam.name,
            awayTeamShortName = match.awayTeam.shortName,
            awayTeamLogoUrl = match.awayTeam.logoUrl ?: "",
            homeScore = match.homeScore,
            awayScore = match.awayScore,
            status = match.status.name,
            minute = match.minute,
            leagueId = match.league.id,
            leagueName = match.league.name,
            leagueCountry = match.league.country,
            leagueLogoUrl = match.league.logoUrl ?: "",
            dateTime = match.dateTime,
            isFavorite = match.isFavorite
        )
    }
    
    fun mapEntityToMatch(entity: MatchEntity): Match {
        return Match(
            id = entity.id,
            homeTeam = Team(
                id = entity.homeTeamId,
                name = entity.homeTeamName,
                shortName = entity.homeTeamShortName,
                logoUrl = entity.homeTeamLogoUrl
            ),
            awayTeam = Team(
                id = entity.awayTeamId,
                name = entity.awayTeamName,
                shortName = entity.awayTeamShortName,
                logoUrl = entity.awayTeamLogoUrl
            ),
            homeScore = entity.homeScore,
            awayScore = entity.awayScore,
            status = MatchStatus.valueOf(entity.status),
            minute = entity.minute,
            league = League(
                id = entity.leagueId,
                name = entity.leagueName,
                country = entity.leagueCountry,
                logoUrl = entity.leagueLogoUrl
            ),
            dateTime = entity.dateTime,
            events = emptyList(), // События не сохраняем в локальной БД
            isFavorite = entity.isFavorite
        )
    }
    
    fun mapEntityListToMatches(entities: List<MatchEntity>): List<Match> {
        return entities.map { mapEntityToMatch(it) }
    }
    
    fun mapMatchesToEntities(matches: List<Match>): List<MatchEntity> {
        return matches.map { mapMatchToEntity(it) }
    }
} 
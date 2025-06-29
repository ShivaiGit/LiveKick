package com.example.livekick.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.livekick.domain.model.MatchStatus
import java.time.LocalDateTime

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey
    val id: String,
    val homeTeamId: String,
    val homeTeamName: String,
    val homeTeamShortName: String,
    val homeTeamLogoUrl: String,
    val awayTeamId: String,
    val awayTeamName: String,
    val awayTeamShortName: String,
    val awayTeamLogoUrl: String,
    val homeScore: Int,
    val awayScore: Int,
    val status: String,
    val minute: Int?,
    val leagueId: String,
    val leagueName: String,
    val leagueCountry: String,
    val leagueLogoUrl: String,
    val dateTime: LocalDateTime,
    val isFavorite: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
) 
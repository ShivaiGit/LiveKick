package com.example.livekick.presentation.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.livekick.domain.model.*
import kotlinx.coroutines.delay

@Composable
fun AnimatedMatchCard(
    match: Match,
    onMatchClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onMatchClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp // уменьшено
        ),
        shape = RoundedCornerShape(12.dp), // уменьшено
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp) // было 16.dp
        ) {
            // Заголовок с лигой
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = match.league.name,
                    fontSize = 10.sp, // было 12.sp
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                MatchStatusStatic(status = match.status, minute = match.minute)
            }
            Spacer(modifier = Modifier.height(6.dp)) // было 12.dp
            // Команды и счет
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = match.homeTeam.name,
                        fontSize = 12.sp, // было 14.sp
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    if (match.homeTeam.shortName != null) {
                        Text(
                            text = match.homeTeam.shortName,
                            fontSize = 9.sp, // было 10.sp
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
                ScoreStatic(
                    homeScore = match.homeScore,
                    awayScore = match.awayScore,
                    compact = true // новый параметр
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = match.awayTeam.name,
                        fontSize = 12.sp, // было 14.sp
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    if (match.awayTeam.shortName != null) {
                        Text(
                            text = match.awayTeam.shortName,
                            fontSize = 9.sp, // было 10.sp
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp)) // было 12.dp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatMatchTime(match.dateTime),
                    fontSize = 10.sp, // было 12.sp
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FavoriteButtonStatic(
                    isFavorite = match.isFavorite,
                    onClick = onFavoriteClick,
                    compact = true // новый параметр
                )
            }
        }
    }
}

@Composable
fun MatchStatusStatic(status: MatchStatus, minute: Int?) {
    val (backgroundColor, textColor, text) = when (status) {
        MatchStatus.LIVE -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "LIVE ${minute ?: 0}'"
        )
        MatchStatus.FINISHED -> Triple(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
            "ЗАВЕРШЕН"
        )
        MatchStatus.SCHEDULED -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            "ЗАПЛАНИРОВАН"
        )
        MatchStatus.POSTPONED -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "ОТЛОЖЕН"
        )
        MatchStatus.CANCELLED -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "ОТМЕНЕН"
        )
    }
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
fun ScoreStatic(homeScore: Int, awayScore: Int, compact: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 8.dp)
    ) {
        Text(
            text = homeScore.toString(),
            fontSize = if (compact) 16.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "-",
            fontSize = if (compact) 12.sp else 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = awayScore.toString(),
            fontSize = if (compact) 16.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun FavoriteButtonStatic(isFavorite: Boolean, onClick: () -> Unit, compact: Boolean = false) {
    IconButton(onClick = onClick, modifier = Modifier.size(if (compact) 28.dp else 40.dp)) {
        Icon(
            imageVector = if (isFavorite) {
                Icons.Default.Favorite
            } else {
                Icons.Default.FavoriteBorder
            },
            contentDescription = "Избранное",
            tint = if (isFavorite) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(if (compact) 16.dp else 24.dp)
        )
    }
}

private fun formatMatchTime(dateTime: java.time.LocalDateTime): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm")
    return dateTime.format(formatter)
} 
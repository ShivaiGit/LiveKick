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
import androidx.compose.foundation.border

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
            .heightIn(min = 48.dp)
            .clickable { onMatchClick() }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(8.dp)
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Время и статус
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.widthIn(min = 44.dp)
            ) {
                Text(
                    text = formatMatchTime(match.dateTime),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                MatchStatusMaterial(status = match.status, minute = match.minute)
            }
            // Команды и счет
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = match.homeTeam.shortName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.width(6.dp))
            ScoreMaterial(
                homeScore = match.homeScore,
                awayScore = match.awayScore,
                status = match.status
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = match.awayTeam.shortName,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Избранное
            FavoriteButtonStatic(
                isFavorite = match.isFavorite,
                onClick = onFavoriteClick,
                compact = true,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun MatchStatusMaterial(status: MatchStatus, minute: Int?) {
    val (bg, fg, text) = when (status) {
        MatchStatus.LIVE -> Triple(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.primary,
            "LIVE${if (minute != null) " ${minute}'" else ""}"
        )
        MatchStatus.FINISHED -> Triple(
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.secondary,
            "Завершён"
        )
        MatchStatus.SCHEDULED -> Triple(
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.tertiary,
            "Скоро"
        )
        MatchStatus.POSTPONED, MatchStatus.CANCELLED -> Triple(
            MaterialTheme.colorScheme.outlineVariant,
            MaterialTheme.colorScheme.outline,
            "—"
        )
    }
    Surface(
        color = bg,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            color = fg,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            maxLines = 1
        )
    }
}

@Composable
fun ScoreMaterial(homeScore: Int, awayScore: Int, status: MatchStatus) {
    val color = when (status) {
        MatchStatus.LIVE -> MaterialTheme.colorScheme.primary
        MatchStatus.FINISHED -> MaterialTheme.colorScheme.secondary
        MatchStatus.SCHEDULED -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurface
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = homeScore.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = "-",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = awayScore.toString(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun FavoriteButtonStatic(isFavorite: Boolean, onClick: () -> Unit, compact: Boolean = false, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = if (isFavorite) {
                Icons.Default.Favorite
            } else {
                Icons.Default.FavoriteBorder
            },
            contentDescription = "Избранное",
            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(if (compact) 16.dp else 24.dp)
        )
    }
}

private fun formatMatchTime(dateTime: java.time.LocalDateTime): String {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm")
    return dateTime.format(formatter)
} 
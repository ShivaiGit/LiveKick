package com.example.livekick.presentation.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.livekick.domain.model.*

@Composable
fun AdvancedMatchStats(
    match: Match,
    statistics: MatchStatistics?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Детальная статистика",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (statistics != null) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    PossessionChart(statistics.possession)
                    HorizontalDivider(thickness = 1.dp)
                    ShotAnalysis(statistics.shots)
                    HorizontalDivider(thickness = 1.dp)
                    PassAnalysis(statistics.passes)
                    HorizontalDivider(thickness = 1.dp)
                    CardAnalysis(statistics.cards)
                    HorizontalDivider(thickness = 1.dp)
                    OtherStats(
                        corners = statistics.corners,
                        fouls = statistics.fouls,
                        offsides = statistics.offsides,
                        saves = statistics.saves
                    )
                }
            } else {
                // Показываем базовую статистику из матча
                BasicStats(match)
            }
        }
    }
}

@Composable
fun PossessionChart(possession: PossessionStats) {
    Column {
        Text(
            text = "Владение мячом",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Домашняя команда
            Column(
                modifier = Modifier.weight(possession.homePossession.toFloat()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "${possession.homePossession}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // Гостевая команда
            Column(
                modifier = Modifier.weight(possession.awayPossession.toFloat()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.secondary)
                )
                Text(
                    text = "${possession.awayPossession}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun ShotAnalysis(shots: ShotStats) {
    Column {
        Text(
            text = "Удары",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticColumn(
                label = "Всего",
                homeValue = shots.homeShots.toString(),
                awayValue = shots.awayShots.toString()
            )
            
            StatisticColumn(
                label = "В створ",
                homeValue = shots.homeShotsOnTarget.toString(),
                awayValue = shots.awayShotsOnTarget.toString()
            )
            
            StatisticColumn(
                label = "Мимо",
                homeValue = shots.homeShotsOffTarget.toString(),
                awayValue = shots.awayShotsOffTarget.toString()
            )
            
            StatisticColumn(
                label = "Блокировано",
                homeValue = shots.homeBlockedShots.toString(),
                awayValue = shots.awayBlockedShots.toString()
            )
        }
    }
}

@Composable
fun PassAnalysis(passes: PassStats) {
    Column {
        Text(
            text = "Передачи",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticColumn(
                label = "Всего",
                homeValue = passes.homePasses.toString(),
                awayValue = passes.awayPasses.toString()
            )
            
            StatisticColumn(
                label = "Точность",
                homeValue = "${passes.homePassAccuracy}%",
                awayValue = "${passes.awayPassAccuracy}%"
            )
        }
    }
}

@Composable
fun CardAnalysis(cards: CardStats) {
    Column {
        Text(
            text = "Карточки",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticColumn(
                label = "Желтые",
                homeValue = cards.homeYellowCards.toString(),
                awayValue = cards.awayYellowCards.toString(),
                homeColor = Color(0xFFFFEB3B),
                awayColor = Color(0xFFFFEB3B)
            )
            
            StatisticColumn(
                label = "Красные",
                homeValue = cards.homeRedCards.toString(),
                awayValue = cards.awayRedCards.toString(),
                homeColor = Color(0xFFF44336),
                awayColor = Color(0xFFF44336)
            )
        }
    }
}

@Composable
fun OtherStats(
    corners: CornerStats,
    fouls: FoulStats,
    offsides: OffsideStats,
    saves: SaveStats
) {
    Column {
        Text(
            text = "Другие показатели",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticColumn(
                label = "Угловые",
                homeValue = corners.homeCorners.toString(),
                awayValue = corners.awayCorners.toString()
            )
            
            StatisticColumn(
                label = "Фолы",
                homeValue = fouls.homeFouls.toString(),
                awayValue = fouls.awayFouls.toString()
            )
            
            StatisticColumn(
                label = "Офсайды",
                homeValue = offsides.homeOffsides.toString(),
                awayValue = offsides.awayOffsides.toString()
            )
            
            StatisticColumn(
                label = "Сейвы",
                homeValue = saves.homeSaves.toString(),
                awayValue = saves.awaySaves.toString()
            )
        }
    }
}

@Composable
fun StatisticColumn(
    label: String,
    homeValue: String,
    awayValue: String,
    homeColor: Color = MaterialTheme.colorScheme.primary,
    awayColor: Color = MaterialTheme.colorScheme.secondary
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = homeValue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = homeColor
        )
        
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = awayValue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = awayColor
        )
    }
}

@Composable
fun BasicStats(match: Match) {
    Column {
        Text(
            text = "Базовая статистика",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticColumn(
                label = "Счет",
                homeValue = match.homeScore.toString(),
                awayValue = match.awayScore.toString()
            )
            
            StatisticColumn(
                label = "Статус",
                homeValue = when (match.status) {
                    MatchStatus.LIVE -> "LIVE"
                    MatchStatus.FINISHED -> "ЗАВЕРШЕН"
                    MatchStatus.SCHEDULED -> "ЗАПЛАНИРОВАН"
                    MatchStatus.POSTPONED -> "ОТЛОЖЕН"
                    MatchStatus.CANCELLED -> "ОТМЕНЕН"
                },
                awayValue = ""
            )
        }
        
        if (match.status == MatchStatus.LIVE && match.minute != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Время матча: ${match.minute}'",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
} 
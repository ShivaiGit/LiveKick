package com.example.livekick.presentation.screen.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamStatisticsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        // Удаляю topBar
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Топ команд",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                TopTeamsCard()
            }
            
            item {
                Text(
                    text = "Статистика по лигам",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                LeagueStatsCard()
            }
            
            item {
                Text(
                    text = "Лучшие бомбардиры",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                TopScorersCard()
            }
        }
    }
}

@Composable
fun TopTeamsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Топ-5 команд",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val topTeams = listOf(
                TeamStats("Manchester City", 85, 28, 5, 5, 5, 89, 33),
                TeamStats("Arsenal", 81, 26, 6, 6, 6, 88, 43),
                TeamStats("Manchester United", 75, 23, 6, 9, 9, 58, 43),
                TeamStats("Newcastle", 71, 19, 14, 5, 5, 68, 33),
                TeamStats("Liverpool", 67, 19, 10, 9, 9, 75, 47)
            )
            
            topTeams.forEachIndexed { index, team ->
                TeamStatsRow(
                    position = index + 1,
                    team = team,
                    isTopThree = index < 3
                )
                if (index < topTeams.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun TeamStatsRow(
    position: Int,
    team: TeamStats,
    isTopThree: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isTopThree) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Позиция
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (isTopThree) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = position.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isTopThree) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Название команды
        Text(
            text = team.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        
        // Статистика
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatisticItem("О", team.points.toString())
            StatisticItem("И", team.played.toString())
            StatisticItem("В", team.won.toString())
            StatisticItem("Н", team.drawn.toString())
            StatisticItem("П", team.lost.toString())
            StatisticItem("ЗГ", team.goalsFor.toString())
            StatisticItem("ПГ", team.goalsAgainst.toString())
        }
    }
}

@Composable
fun StatisticItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LeagueStatsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Статистика по лигам",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val leagues = listOf(
                LeagueStats("Premier League", 20, 380, 2.7),
                LeagueStats("La Liga", 20, 380, 2.6),
                LeagueStats("Bundesliga", 18, 306, 3.1),
                LeagueStats("Serie A", 20, 380, 2.8),
                LeagueStats("Ligue 1", 20, 380, 2.5)
            )
            
            leagues.forEach { league ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = league.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${league.teams} команд, ${league.matches} матчей",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${league.avgGoals} гола/матч",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun TopScorersCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Лучшие бомбардиры",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val scorers = listOf(
                PlayerStats("Erling Haaland", "Manchester City", 36, 8),
                PlayerStats("Harry Kane", "Tottenham", 30, 3),
                PlayerStats("Ivan Toney", "Brentford", 20, 4),
                PlayerStats("Mohamed Salah", "Liverpool", 19, 12),
                PlayerStats("Callum Wilson", "Newcastle", 18, 5)
            )
            
            scorers.forEachIndexed { index, player ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = player.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = player.team,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "${player.goals} голов",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${player.assists} передач",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (index < scorers.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// Вспомогательные классы данных
data class TeamStats(
    val name: String,
    val points: Int,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int
)

data class LeagueStats(
    val name: String,
    val teams: Int,
    val matches: Int,
    val avgGoals: Double
)

data class PlayerStats(
    val name: String,
    val team: String,
    val goals: Int,
    val assists: Int
) 
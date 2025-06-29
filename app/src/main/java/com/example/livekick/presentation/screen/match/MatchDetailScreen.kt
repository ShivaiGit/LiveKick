package com.example.livekick.presentation.screen.match

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.livekick.domain.model.*
import com.example.livekick.presentation.component.MatchCard
import com.example.livekick.presentation.viewmodel.MatchDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    navController: NavController,
    matchId: String,
    context: Context,
    viewModel: MatchDetailViewModel = viewModel { MatchDetailViewModel(context) }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Загружаем матч при первом входе
    LaunchedEffect(matchId) {
        viewModel.loadMatch(matchId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Детали матча",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (uiState.match?.isFavorite == true) {
                                Icons.Default.Favorite
                            } else {
                                Icons.Default.FavoriteBorder
                            },
                            contentDescription = "Избранное"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            uiState.match == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Матч не найден",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        // Карточка матча
                        MatchCard(
                            match = uiState.match!!,
                            onMatchClick = { },
                            onFavoriteClick = { viewModel.toggleFavorite() }
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Статистика матча
                        MatchStatisticsCard(match = uiState.match!!)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // События матча
                        if (uiState.match!!.events.isNotEmpty()) {
                            MatchEventsCard(events = uiState.match!!.events)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchStatisticsCard(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Статистика",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Домашняя команда
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = match.homeTeam.shortName ?: match.homeTeam.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = match.homeScore.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                // Разделитель
                Text(
                    text = "VS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Гостевая команда
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = match.awayTeam.shortName ?: match.awayTeam.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = match.awayScore.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Дополнительная информация
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Статус",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = when (match.status) {
                            MatchStatus.LIVE -> "В игре"
                            MatchStatus.FINISHED -> "Завершен"
                            MatchStatus.SCHEDULED -> "Запланирован"
                            MatchStatus.CANCELLED -> "Отменен"
                            MatchStatus.POSTPONED -> "Отложен"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column {
                    Text(
                        text = "Лига",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = match.league.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Column {
                    Text(
                        text = "Время",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${match.dateTime.hour}:${match.dateTime.minute.toString().padStart(2, '0')}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MatchEventsCard(events: List<MatchEvent>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "События матча",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            events.forEach { event ->
                EventItem(event = event)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun EventItem(event: MatchEvent) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Минута
        Text(
            text = "${event.minute}'",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(40.dp)
        )
        
        // Иконка события
        Icon(
            imageVector = when (event.type) {
                EventType.GOAL -> Icons.Default.Favorite // Временно используем иконку сердца
                EventType.YELLOW_CARD -> Icons.Default.Favorite
                EventType.RED_CARD -> Icons.Default.Favorite
                EventType.SUBSTITUTION -> Icons.Default.Favorite
                EventType.CORNER -> Icons.Default.Favorite
                EventType.FOUL -> Icons.Default.Favorite
            },
            contentDescription = event.type.name,
            tint = when (event.type) {
                EventType.GOAL -> MaterialTheme.colorScheme.primary
                EventType.YELLOW_CARD -> MaterialTheme.colorScheme.tertiary
                EventType.RED_CARD -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Описание события
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.description ?: "",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = event.player ?: "",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Команда
        Text(
            text = event.team.shortName ?: event.team.name,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 
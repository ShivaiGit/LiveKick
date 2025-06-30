package com.example.livekick.presentation.screen.match

import android.content.Context
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.livekick.data.repository.MatchRepositoryImpl
import com.example.livekick.domain.model.*
import com.example.livekick.presentation.component.MatchCard
import com.example.livekick.presentation.component.LiveMatchStats
import com.example.livekick.presentation.component.MatchProgressBar
import com.example.livekick.presentation.component.AdvancedMatchStats
import com.example.livekick.presentation.viewmodel.MatchDetailViewModel
import com.example.livekick.presentation.viewmodel.MatchDetailViewModelFactory
import com.example.livekick.data.remote.dto.MatchStatisticsResponse
import com.example.livekick.data.remote.dto.MatchEventResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: String,
    matchRepository: MatchRepositoryImpl,
    onNavigateBack: () -> Unit,
    viewModel: MatchDetailViewModel = viewModel(
        factory = MatchDetailViewModelFactory(matchRepository)
    )
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
                    IconButton(onClick = onNavigateBack) {
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = "Матч не найден",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Попробуйте обновить данные",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp), // отступ снизу под бар
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        item { MatchHeaderCard(match = uiState.match!!) }
                        item { Divider(thickness = 1.5.dp) }
                        item { MatchProgressBar(match = uiState.match!!) }
                        item { Divider(thickness = 1.5.dp) }
                        item { LiveMatchStats(match = uiState.match!!) }
                        item { Divider(thickness = 1.5.dp) }
                        item { if (uiState.statistics.isNotEmpty()) MatchStatisticsBlock(statistics = uiState.statistics) }
                        item { Divider(thickness = 1.5.dp) }
                        item { if (uiState.events.isNotEmpty()) MatchEventsBlock(events = uiState.events) }
                        item { Divider(thickness = 1.5.dp) }
                        item { if (uiState.events.isNotEmpty()) GoalScorersBlock(events = uiState.events) }
                        item { Divider(thickness = 1.5.dp) }
                        item { MatchStatisticsCard(match = uiState.match!!) }
                        item { Divider(thickness = 1.5.dp) }
                        item { LeagueInfoCard(match = uiState.match!!) }
                    }
                    MatchDetailBottomBar(
                        onRefresh = { viewModel.refresh() },
                        onSettings = { /* TODO: Навигация к настройкам */ },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun MatchHeaderCard(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Лига
            Text(
                text = match.league.name,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Команды и счет
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Домашняя команда
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = match.homeTeam.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    if (match.homeTeam.shortName != null) {
                        Text(
                            text = match.homeTeam.shortName,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Счет
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = match.homeScore.toString(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "-",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = match.awayScore.toString(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Статус матча
                    MatchStatusChip(status = match.status, minute = match.minute)
                }
                
                // Гостевая команда
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = match.awayTeam.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    if (match.awayTeam.shortName != null) {
                        Text(
                            text = match.awayTeam.shortName,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Дата и время
            Text(
                text = formatMatchDateTime(match.dateTime),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MatchStatusChip(status: MatchStatus, minute: Int?) {
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
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
fun MatchStatisticsCard(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Статистика матча",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Основная статистика
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    label = "Владение",
                    homeValue = "55%",
                    awayValue = "45%"
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    label = "Удары",
                    homeValue = "12",
                    awayValue = "8"
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatisticItem(
                    label = "Удары в створ",
                    homeValue = "5",
                    awayValue = "3"
                )
            }
        }
    }
}

@Composable
fun StatisticItem(
    label: String,
    homeValue: String,
    awayValue: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = homeValue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "vs",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = awayValue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun MatchStatisticsBlock(statistics: List<MatchStatisticsResponse>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Статистика", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            statistics.forEach { stat ->
                Text("${stat.teamName}: удары в створ: ${stat.shotsOnTarget ?: 0}, владение: ${stat.possession ?: 0}%")
            }
        }
    }
}

@Composable
fun MatchEventsBlock(events: List<MatchEventResponse>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Таймлайн событий", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            if (events.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Нет событий в этом матче", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val sortedEvents = events.sortedBy { it.minute ?: 0 }
                Column(modifier = Modifier.fillMaxWidth()) {
                    sortedEvents.forEachIndexed { idx, event ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Линия таймлайна
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(32.dp)
                            ) {
                                if (idx != 0) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(12.dp)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                    )
                                }
                                EventIcon(event.type)
                                if (idx != events.lastIndex) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(12.dp)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${event.minute ?: "-"}'",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = eventDescription(event),
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (!event.playerName.isNullOrBlank()) {
                                    Text(
                                        text = event.playerName + (if (!event.teamName.isNullOrBlank()) " (${event.teamName})" else ""),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else if (!event.teamName.isNullOrBlank()) {
                                    Text(
                                        text = event.teamName,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        if (idx != events.lastIndex) {
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventIcon(type: String?) {
    val (icon, tint) = when (type?.lowercase()) {
        "goal" -> Icons.Default.SportsSoccer to MaterialTheme.colorScheme.primary
        "yellowcard" -> Icons.Default.Warning to Color(0xFFFFD600)
        "redcard" -> Icons.Default.Warning to Color(0xFFD32F2F)
        "substitution" -> Icons.Default.SwapHoriz to MaterialTheme.colorScheme.tertiary
        "corner" -> Icons.Default.Flag to MaterialTheme.colorScheme.secondary
        "foul" -> Icons.Default.Block to MaterialTheme.colorScheme.error
        else -> Icons.Default.Info to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(20.dp)
    )
}

private fun eventDescription(event: MatchEventResponse): String {
    return when (event.type?.lowercase()) {
        "goal" -> "Гол"
        "yellowcard" -> "Жёлтая карточка"
        "redcard" -> "Красная карточка"
        "substitution" -> "Замена"
        "corner" -> "Угловой"
        "foul" -> "Нарушение"
        else -> event.type ?: "Событие"
    }
}

@Composable
fun GoalScorersBlock(events: List<MatchEventResponse>) {
    val goals = events.filter { it.type?.equals("goal", ignoreCase = true) == true }
    if (goals.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Авторы голов", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Text("Нет информации о голах")
            }
        }
        return
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Авторы голов", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            goals.forEach { goal ->
                Text("${goal.minute ?: "-"}' ${goal.playerName ?: "Неизвестно"} (${goal.teamName ?: ""})")
            }
        }
    }
}

@Composable
fun LeagueInfoCard(match: Match) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Информация о лиге",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Лига:",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = match.league.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Страна:",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = match.league.country,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun getEventDescription(event: MatchEvent): String {
    return when (event.type) {
        EventType.GOAL -> "Гол"
        EventType.YELLOW_CARD -> "Желтая карточка"
        EventType.RED_CARD -> "Красная карточка"
        EventType.SUBSTITUTION -> "Замена"
        EventType.CORNER -> "Угловой"
        EventType.FOUL -> "Нарушение"
    }
}

private fun formatMatchDateTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return dateTime.format(formatter)
}

@Composable
fun MatchDetailBottomBar(
    onRefresh: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Обновить",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Настройки",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
} 
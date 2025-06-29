package com.example.livekick.presentation.screen.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.livekick.data.repository.MatchRepositoryImpl
import com.example.livekick.domain.model.Match
import com.example.livekick.domain.model.MatchStatus
import com.example.livekick.presentation.component.MatchCard
import com.example.livekick.presentation.component.SearchAndFilterBar
import com.example.livekick.presentation.component.AnimatedMatchCard
import com.example.livekick.presentation.component.AnimatedLoadingIndicator
import com.example.livekick.presentation.viewmodel.HomeViewModel
import com.example.livekick.presentation.viewmodel.HomeViewModelFactory
import com.example.livekick.ui.theme.LocalThemeManager
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    matchRepository: MatchRepositoryImpl,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(matchRepository)
    ),
    onNavigateToMatch: (String) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToStatistics: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val selectedLeague by viewModel.selectedLeague.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val availableLeagues by viewModel.availableLeagues.collectAsState()
    
    val themeManager = LocalThemeManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Заголовок
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LiveKick",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { themeManager.toggleTheme() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Переключить тему",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(
                    onClick = { viewModel.onRefresh() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Обновить",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(
                    onClick = onNavigateToFavorites
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Избранное",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(
                    onClick = onNavigateToSettings
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Настройки",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(
                    onClick = onNavigateToStatistics
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Статистика",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Поиск и фильтры
        SearchAndFilterBar(
            searchQuery = searchQuery,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            selectedStatus = selectedStatus,
            onStatusFilterChange = viewModel::onStatusFilterChange,
            selectedLeague = selectedLeague,
            onLeagueFilterChange = viewModel::onLeagueFilterChange,
            selectedDate = selectedDate,
            onDateFilterChange = viewModel::onDateFilterChange,
            availableLeagues = availableLeagues
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Содержимое
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnimatedLoadingIndicator(
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Загрузка матчей...",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { viewModel.onRefresh() }
                        ) {
                            Text("Повторить")
                        }
                    }
                }
            }
            
            uiState.matches.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Нет активных матчей",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Попробуйте изменить фильтры или обновить данные",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.matches,
                        key = { it.id }
                    ) { match ->
                        AnimatedMatchCard(
                            match = match,
                            onMatchClick = { onNavigateToMatch(match.id) },
                            onFavoriteClick = { viewModel.onToggleFavorite(match) },
                            isVisible = true
                        )
                    }
                }
            }
        }
    }
} 
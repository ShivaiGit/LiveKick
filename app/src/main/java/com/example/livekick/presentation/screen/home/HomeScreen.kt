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
import com.example.livekick.presentation.component.LottieLoadingIndicator
import com.example.livekick.presentation.viewmodel.HomeViewModel
import com.example.livekick.presentation.viewmodel.HomeViewModelFactory
import com.example.livekick.ui.theme.LocalThemeManager
import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    matchRepository: MatchRepositoryImpl,
    showLiveOnly: Boolean = false,
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
                        LottieLoadingIndicator(
                            modifier = Modifier.size(140.dp)
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
            
            else -> {
                val matches = if (showLiveOnly) {
                    uiState.matches.filter { it.status == MatchStatus.LIVE }
                } else {
                    uiState.matches
                }
                if (matches.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (showLiveOnly) "Нет live-матчей" else "Нет матчей",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(matches) { match ->
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
} 
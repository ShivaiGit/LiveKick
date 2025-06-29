package com.example.livekick.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.livekick.data.repository.MatchRepositoryImpl
import com.example.livekick.domain.model.Match
import com.example.livekick.domain.model.MatchStatus
import com.example.livekick.domain.usecase.GetLiveMatchesUseCase
import com.example.livekick.domain.usecase.ToggleFavoriteMatchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val context: Context
) : ViewModel() {
    
    private val matchRepository = MatchRepositoryImpl(context)
    private val getLiveMatchesUseCase = GetLiveMatchesUseCase(matchRepository)
    private val toggleFavoriteMatchUseCase = ToggleFavoriteMatchUseCase(matchRepository)
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // Фильтры
    private val _searchQuery = MutableStateFlow("")
    private val _selectedStatus = MutableStateFlow<MatchStatus?>(null)
    private val _selectedLeague = MutableStateFlow<String?>(null)
    
    init {
        loadLiveMatches()
        setupFilteredMatches()
    }
    
    private fun loadLiveMatches() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            getLiveMatchesUseCase().collect { matches ->
                _uiState.value = _uiState.value.copy(
                    allMatches = matches,
                    isLoading = false
                )
                // Применяем фильтры к новым данным
                applyFilters(matches)
            }
        }
    }
    
    private fun applyFilters(allMatches: List<Match>) {
        val filteredMatches = filterMatches(
            allMatches,
            _searchQuery.value,
            _selectedStatus.value,
            _selectedLeague.value
        )
        _uiState.value = _uiState.value.copy(
            matches = filteredMatches
        )
    }
    
    private fun setupFilteredMatches() {
        // Инициализация не нужна, фильтры применяются в loadLiveMatches
    }
    
    private fun filterMatches(
        matches: List<Match>,
        query: String,
        status: MatchStatus?,
        league: String?
    ): List<Match> {
        return matches.filter { match ->
            // Фильтр по поиску
            val matchesSearch = query.isEmpty() || 
                match.homeTeam.name.contains(query, ignoreCase = true) ||
                match.homeTeam.shortName.contains(query, ignoreCase = true) ||
                match.awayTeam.name.contains(query, ignoreCase = true) ||
                match.awayTeam.shortName.contains(query, ignoreCase = true)
            
            // Фильтр по статусу
            val matchesStatus = status == null || match.status == status
            
            // Фильтр по лиге
            val matchesLeague = league == null || match.league.name == league
            
            matchesSearch && matchesStatus && matchesLeague
        }
    }
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters(_uiState.value.allMatches)
    }
    
    fun updateStatusFilter(status: MatchStatus?) {
        _selectedStatus.value = status
        applyFilters(_uiState.value.allMatches)
    }
    
    fun updateLeagueFilter(league: String?) {
        _selectedLeague.value = league
        applyFilters(_uiState.value.allMatches)
    }
    
    fun clearFilters() {
        _searchQuery.value = ""
        _selectedStatus.value = null
        _selectedLeague.value = null
        applyFilters(_uiState.value.allMatches)
    }
    
    fun toggleFavorite(matchId: String) {
        viewModelScope.launch {
            toggleFavoriteMatchUseCase(matchId)
        }
    }
    
    fun refreshMatches() {
        loadLiveMatches()
    }
}

data class HomeUiState(
    val matches: List<Match> = emptyList(),
    val allMatches: List<Match> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val availableLeagues: List<String>
        get() = allMatches.map { it.league.name }.distinct().sorted()
} 
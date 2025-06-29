package com.example.livekick.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.livekick.data.local.AppDatabase
import com.example.livekick.data.local.repository.LocalMatchRepository
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
import java.time.LocalDate

class HomeViewModel(
    private val getLiveMatchesUseCase: GetLiveMatchesUseCase,
    private val toggleFavoriteMatchUseCase: ToggleFavoriteMatchUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    // Фильтры
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _selectedStatus = MutableStateFlow<MatchStatus?>(null)
    val selectedStatus: StateFlow<MatchStatus?> = _selectedStatus.asStateFlow()
    
    private val _selectedLeague = MutableStateFlow<String?>(null)
    val selectedLeague: StateFlow<String?> = _selectedLeague.asStateFlow()
    
    private val _selectedDate = MutableStateFlow<String?>(null)
    val selectedDate: StateFlow<String?> = _selectedDate.asStateFlow()
    
    private val _availableLeagues = MutableStateFlow<List<String>>(emptyList())
    val availableLeagues: StateFlow<List<String>> = _availableLeagues.asStateFlow()
    
    private var allMatches = listOf<Match>()
    
    init {
        loadMatches()
    }
    
    private fun loadMatches() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                getLiveMatchesUseCase().collect { matches ->
                    allMatches = matches
                    
                    // Обновляем доступные лиги
                    val leagues = matches.map { it.league.name }.distinct().sortedBy { it }
                    _availableLeagues.value = leagues
                    
                    filterMatches()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Неизвестная ошибка"
                )
            }
        }
    }
    
    private fun filterMatches() {
        var filteredMatches = allMatches
        
        // Фильтр по поиску
        val query = _searchQuery.value
        if (query.isNotEmpty()) {
            filteredMatches = filteredMatches.filter { match ->
                match.homeTeam.name.contains(query, ignoreCase = true) ||
                match.awayTeam.name.contains(query, ignoreCase = true) ||
                match.league.name.contains(query, ignoreCase = true)
            }
        }
        
        // Фильтр по статусу
        val status = _selectedStatus.value
        if (status != null) {
            filteredMatches = filteredMatches.filter { it.status == status }
        }
        
        // Фильтр по лиге
        val league = _selectedLeague.value
        if (league != null) {
            filteredMatches = filteredMatches.filter { it.league.name == league }
        }
        
        // Фильтр по дате
        val date = _selectedDate.value
        if (date != null) {
            val today = LocalDate.now()
            filteredMatches = when (date) {
                "today" -> filteredMatches.filter { 
                    val matchDate = it.dateTime.toLocalDate()
                    matchDate == today
                }
                "tomorrow" -> filteredMatches.filter { 
                    val matchDate = it.dateTime.toLocalDate()
                    matchDate == today.plusDays(1)
                }
                "yesterday" -> filteredMatches.filter { 
                    val matchDate = it.dateTime.toLocalDate()
                    matchDate == today.minusDays(1)
                }
                "this_week" -> filteredMatches.filter { 
                    val matchDate = it.dateTime.toLocalDate()
                    val weekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
                    val weekEnd = weekStart.plusDays(6)
                    matchDate in weekStart..weekEnd
                }
                else -> filteredMatches
            }
        }
        
        _uiState.value = _uiState.value.copy(
            matches = filteredMatches,
            isLoading = false,
            error = null
        )
    }
    
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterMatches()
    }
    
    fun onStatusFilterChange(status: MatchStatus?) {
        _selectedStatus.value = status
        filterMatches()
    }
    
    fun onLeagueFilterChange(league: String?) {
        _selectedLeague.value = league
        filterMatches()
    }
    
    fun onDateFilterChange(date: String?) {
        _selectedDate.value = date
        filterMatches()
    }
    
    fun onRefresh() {
        loadMatches()
    }
    
    fun onToggleFavorite(match: Match) {
        viewModelScope.launch {
            try {
                toggleFavoriteMatchUseCase(match.id)
                // Обновляем список матчей после изменения избранного
                loadMatches()
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }
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

// Фабрика для создания HomeViewModel с контекстом
class HomeViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val localRepository = LocalMatchRepository(AppDatabase.getDatabase(context))
            val remoteRepository = MatchRepositoryImpl(context)
            val getLiveMatchesUseCase = GetLiveMatchesUseCase(remoteRepository)
            val toggleFavoriteMatchUseCase = ToggleFavoriteMatchUseCase(remoteRepository)
            
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(getLiveMatchesUseCase, toggleFavoriteMatchUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 
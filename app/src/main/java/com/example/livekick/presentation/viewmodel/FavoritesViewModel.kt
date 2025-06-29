package com.example.livekick.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.livekick.data.repository.MatchRepositoryImpl
import com.example.livekick.domain.model.Match
import com.example.livekick.domain.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val matchRepository: MatchRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()
    
    init {
        loadFavoriteMatches()
    }
    
    private fun loadFavoriteMatches() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                matchRepository.getFavoriteMatches().collect { matches ->
                    _uiState.value = _uiState.value.copy(
                        matches = matches,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Ошибка загрузки избранных матчей"
                )
            }
        }
    }
    
    fun toggleFavorite(matchId: String) {
        viewModelScope.launch {
            try {
                matchRepository.toggleFavorite(matchId)
                // Перезагружаем список после изменения
                loadFavoriteMatches()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Ошибка обновления избранного"
                )
            }
        }
    }
    
    fun refreshFavorites() {
        loadFavoriteMatches()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class FavoritesUiState(
    val matches: List<Match> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// Фабрика для создания FavoritesViewModel с репозиторием
class FavoritesViewModelFactory(
    private val matchRepository: MatchRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoritesViewModel(matchRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 
package com.example.livekick.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.livekick.data.repository.MatchRepositoryImpl
import com.example.livekick.domain.model.Match
import com.example.livekick.domain.repository.MatchRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val context: Context
) : ViewModel() {
    
    private val matchRepository: MatchRepository = MatchRepositoryImpl(context)
    
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
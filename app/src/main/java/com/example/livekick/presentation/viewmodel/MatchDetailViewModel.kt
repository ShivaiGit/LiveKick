package com.example.livekick.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.livekick.data.repository.MatchRepositoryImpl
import com.example.livekick.domain.model.Match
import com.example.livekick.domain.usecase.GetMatchByIdUseCase
import com.example.livekick.domain.usecase.ToggleFavoriteMatchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MatchDetailViewModel(
    private val context: Context
) : ViewModel() {
    
    private val matchRepository = MatchRepositoryImpl(context)
    private val getMatchByIdUseCase = GetMatchByIdUseCase(matchRepository)
    private val toggleFavoriteMatchUseCase = ToggleFavoriteMatchUseCase(matchRepository)
    
    private val _uiState = MutableStateFlow(MatchDetailUiState())
    val uiState: StateFlow<MatchDetailUiState> = _uiState.asStateFlow()
    
    private var currentMatchId: String? = null
    
    fun loadMatch(matchId: String) {
        currentMatchId = matchId
        loadMatchDetails()
    }
    
    private fun loadMatchDetails() {
        val matchId = currentMatchId ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                getMatchByIdUseCase(matchId).collect { match ->
                    _uiState.value = _uiState.value.copy(
                        match = match,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Ошибка загрузки матча",
                    isLoading = false
                )
            }
        }
    }
    
    fun toggleFavorite() {
        val matchId = currentMatchId ?: return
        
        viewModelScope.launch {
            try {
                toggleFavoriteMatchUseCase(matchId)
                // Обновляем UI после изменения статуса избранного
                _uiState.value = _uiState.value.copy(
                    match = _uiState.value.match?.copy(
                        isFavorite = !(_uiState.value.match?.isFavorite ?: false)
                    )
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Ошибка изменения статуса избранного"
                )
            }
        }
    }
    
    fun refresh() {
        loadMatchDetails()
    }
}

data class MatchDetailUiState(
    val match: Match? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) 
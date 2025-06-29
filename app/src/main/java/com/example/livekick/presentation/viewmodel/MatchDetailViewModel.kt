package com.example.livekick.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.livekick.domain.model.Match
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MatchDetailViewModel() : ViewModel() {
    
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
                // Временно показываем заглушку
                _uiState.value = _uiState.value.copy(
                    match = null,
                    isLoading = false,
                    error = "Функция временно недоступна"
                )
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
                // Временно ничего не делаем
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
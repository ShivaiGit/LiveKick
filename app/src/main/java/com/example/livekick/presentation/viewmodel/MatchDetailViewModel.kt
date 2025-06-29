package com.example.livekick.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
    private val getMatchByIdUseCase: GetMatchByIdUseCase,
    private val toggleFavoriteMatchUseCase: ToggleFavoriteMatchUseCase
) : ViewModel() {
    
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
                        isLoading = false,
                        error = null
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
                // Обновляем данные матча после изменения статуса
                loadMatchDetails()
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

// Фабрика для создания MatchDetailViewModel с репозиторием
class MatchDetailViewModelFactory(
    private val matchRepository: MatchRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchDetailViewModel::class.java)) {
            val getMatchByIdUseCase = GetMatchByIdUseCase(matchRepository)
            val toggleFavoriteMatchUseCase = ToggleFavoriteMatchUseCase(matchRepository)
            
            @Suppress("UNCHECKED_CAST")
            return MatchDetailViewModel(getMatchByIdUseCase, toggleFavoriteMatchUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 
package com.example.livekick.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.livekick.data.repository.MatchRepositoryImpl
import com.example.livekick.domain.model.Match
import com.example.livekick.domain.usecase.GetLiveMatchesUseCase
import com.example.livekick.domain.usecase.ToggleFavoriteMatchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val context: Context
) : ViewModel() {
    
    private val matchRepository = MatchRepositoryImpl(context)
    private val getLiveMatchesUseCase = GetLiveMatchesUseCase(matchRepository)
    private val toggleFavoriteMatchUseCase = ToggleFavoriteMatchUseCase(matchRepository)
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadLiveMatches()
    }
    
    private fun loadLiveMatches() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            getLiveMatchesUseCase().collect { matches ->
                _uiState.value = _uiState.value.copy(
                    matches = matches,
                    isLoading = false
                )
            }
        }
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
    val isLoading: Boolean = false,
    val error: String? = null
) 
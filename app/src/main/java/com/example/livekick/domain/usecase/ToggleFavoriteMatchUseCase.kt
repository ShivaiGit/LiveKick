package com.example.livekick.domain.usecase

import com.example.livekick.domain.repository.MatchRepository

class ToggleFavoriteMatchUseCase(
    private val repository: MatchRepository
) {
    suspend operator fun invoke(matchId: String) = repository.toggleFavorite(matchId)
} 
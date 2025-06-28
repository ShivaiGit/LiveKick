package com.example.livekick.domain.usecase

import com.example.livekick.domain.repository.MatchRepository
import javax.inject.Inject

class ToggleFavoriteMatchUseCase @Inject constructor(
    private val repository: MatchRepository
) {
    suspend operator fun invoke(matchId: String) = repository.toggleFavorite(matchId)
} 
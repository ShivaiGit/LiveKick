package com.example.livekick.domain.usecase

import com.example.livekick.domain.model.Match
import com.example.livekick.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow

class GetLiveMatchesUseCase(
    private val repository: MatchRepository
) {
    operator fun invoke(): Flow<List<Match>> = repository.getLiveMatches()
} 
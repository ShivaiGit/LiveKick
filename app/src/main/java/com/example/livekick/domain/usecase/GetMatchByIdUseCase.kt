package com.example.livekick.domain.usecase

import com.example.livekick.domain.model.Match
import com.example.livekick.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow

class GetMatchByIdUseCase(
    private val repository: MatchRepository
) {
    operator fun invoke(matchId: String): Flow<Match?> {
        return repository.getMatchById(matchId)
    }
} 
package com.example.livekick.domain.usecase

import com.example.livekick.domain.model.Match
import com.example.livekick.domain.repository.MatchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLiveMatchesUseCase @Inject constructor(
    private val repository: MatchRepository
) {
    operator fun invoke(): Flow<List<Match>> = repository.getLiveMatches()
} 
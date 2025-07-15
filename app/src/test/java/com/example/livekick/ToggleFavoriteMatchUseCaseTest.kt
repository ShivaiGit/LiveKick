package com.example.livekick

import com.example.livekick.domain.repository.MatchRepository
import com.example.livekick.domain.usecase.ToggleFavoriteMatchUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ToggleFavoriteMatchUseCaseTest {
    private val repository = mockk<MatchRepository>(relaxed = true)
    private val useCase = ToggleFavoriteMatchUseCase(repository)

    @Test
    fun `invoke calls repository toggleFavorite with correct matchId`() = runTest {
        val matchId = "test_id"
        useCase(matchId)
        coVerify { repository.toggleFavorite(matchId) }
    }
} 
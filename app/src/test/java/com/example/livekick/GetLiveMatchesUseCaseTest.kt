package com.example.livekick

import app.cash.turbine.test
import com.example.livekick.domain.model.*
import com.example.livekick.domain.repository.MatchRepository
import com.example.livekick.domain.usecase.GetLiveMatchesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetLiveMatchesUseCaseTest {
    private val repository = mockk<MatchRepository>()
    private val useCase = GetLiveMatchesUseCase(repository)

    @Test
    fun `invoke returns live matches from repository`() = runTest {
        val match = Match(
            id = "1",
            homeTeam = Team("1", "Team A", "A"),
            awayTeam = Team("2", "Team B", "B"),
            homeScore = 1,
            awayScore = 0,
            status = MatchStatus.LIVE,
            minute = 45,
            league = League("1", "Premier League", "England"),
            dateTime = java.time.LocalDateTime.now()
        )
        coEvery { repository.getLiveMatches() } returns flowOf(listOf(match))

        useCase().test {
            val result = awaitItem()
            assert(result.size == 1)
            assert(result[0].id == "1")
            awaitComplete()
        }
    }
} 
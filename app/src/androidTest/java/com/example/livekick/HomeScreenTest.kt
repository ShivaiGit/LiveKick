package com.example.livekick

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.livekick.domain.model.*
import com.example.livekick.presentation.viewmodel.HomeUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun loadingIndicator_isDisplayed_whenLoading() {
        TestActivity.composable = {
            androidx.compose.material3.Text("Загрузка матчей...")
        }
        composeTestRule.onNodeWithText("Загрузка матчей...").assertIsDisplayed()
    }

    @Test
    fun matchesList_isDisplayed_whenMatchesLoaded() {
        val match = Match(
            id = "1",
            homeTeam = Team("1", "Team A", "A"),
            awayTeam = Team("2", "Team B", "B"),
            homeScore = 2,
            awayScore = 1,
            status = MatchStatus.LIVE,
            minute = 55,
            league = League("1", "Premier League", "England"),
            dateTime = LocalDateTime.now()
        )
        TestActivity.composable = {
            androidx.compose.material3.Text(match.homeTeam.name)
            androidx.compose.material3.Text(match.awayTeam.name)
        }
        composeTestRule.onNodeWithText("Team A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Team B").assertIsDisplayed()
    }
} 
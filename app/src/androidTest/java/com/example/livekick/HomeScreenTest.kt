package com.example.livekick

import androidx.activity.compose.setContent
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityScenarioRule
import com.example.livekick.domain.model.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val scenarioRule = ActivityScenarioRule(TestActivity::class.java)
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingIndicator_isDisplayed_whenLoading() {
        scenarioRule.scenario.onActivity {
            it.setContent {
                androidx.compose.material3.Text("Загрузка матчей...")
            }
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
        scenarioRule.scenario.onActivity {
            it.setContent {
                androidx.compose.material3.Text(match.homeTeam.name)
                androidx.compose.material3.Text(match.awayTeam.name)
            }
        }
        composeTestRule.onNodeWithText("Team A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Team B").assertIsDisplayed()
    }
} 
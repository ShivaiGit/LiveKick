package com.example.livekick

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesScreenUiTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun favoritesScreen_showsLoadingIndicator() {
        // Проверяем, что индикатор загрузки отображается при старте
        composeTestRule.onNodeWithText("Загружаем избранное...").assertIsDisplayed()
    }
} 
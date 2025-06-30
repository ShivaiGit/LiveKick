package com.example.livekick.data.local.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.livekick.ui.theme.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferencesDataStore(private val context: Context) {
    companion object {
        private val LANGUAGE_KEY = preferencesKey<String>("language")
    }

    val languageFlow: Flow<Language> = context.dataStore.data.map { prefs ->
        when (prefs[LANGUAGE_KEY]) {
            "RU" -> Language.RU
            "EN" -> Language.EN
            else -> Language.EN // По умолчанию EN
        }
    }

    suspend fun setLanguage(language: Language) {
        context.dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = language.name
        }
    }
} 
package com.example.livekick.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.livekick.notification.LiveKickNotificationManager
import com.example.livekick.notification.NotificationScheduler
import com.example.livekick.ui.theme.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class NotificationSettingsViewModel(
    private val context: Context
) : ViewModel() {
    
    private val notificationManager = LiveKickNotificationManager(context)
    private val notificationScheduler = NotificationScheduler(context)
    
    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
        observeLanguage()
    }
    
    private fun loadSettings() {
        // В реальном приложении здесь загружались бы настройки из DataStore
        // Пока используем значения по умолчанию
        _uiState.value = NotificationSettingsUiState(
            notificationsEnabled = true,
            matchStartNotifications = true,
            eventNotifications = true,
            favoritesOnly = false,
            quietModeEnabled = false,
            quietModeStart = "22:00",
            quietModeEnd = "08:00"
        )
    }
    
    private fun observeLanguage() {
        viewModelScope.launch {
            // userPrefs.languageFlow.collect { lang ->
            //     _uiState.value = _uiState.value.copy(language = lang)
            // }
        }
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
        
        if (enabled) {
            notificationScheduler.startPeriodicNotifications()
        } else {
            notificationScheduler.stopPeriodicNotifications()
            notificationManager.cancelAllNotifications()
        }
        
        saveSettings()
    }
    
    fun setMatchStartNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(matchStartNotifications = enabled)
        saveSettings()
    }
    
    fun setEventNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(eventNotifications = enabled)
        saveSettings()
    }
    
    fun setFavoritesOnly(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(favoritesOnly = enabled)
        saveSettings()
    }
    
    fun setQuietModeEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(quietModeEnabled = enabled)
        saveSettings()
    }
    
    fun testNotification() {
        if (_uiState.value.notificationsEnabled) {
            notificationManager.showGeneralNotification(
                "Тест уведомления",
                "Это тестовое уведомление от LiveKick!"
            )
        }
    }
    
    fun setLanguage(language: Language) {
        viewModelScope.launch {
            // userPrefs.setLanguage(language)
        }
    }
    
    private fun saveSettings() {
        // В реальном приложении здесь сохранялись бы настройки в DataStore
        viewModelScope.launch {
            // Сохранение настроек
        }
    }
}

data class NotificationSettingsUiState(
    val notificationsEnabled: Boolean = false,
    val matchStartNotifications: Boolean = false,
    val eventNotifications: Boolean = false,
    val favoritesOnly: Boolean = false,
    val quietModeEnabled: Boolean = false,
    val quietModeStart: String = "22:00",
    val quietModeEnd: String = "08:00",
    val language: Language = Language.EN
)

class NotificationSettingsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationSettingsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 
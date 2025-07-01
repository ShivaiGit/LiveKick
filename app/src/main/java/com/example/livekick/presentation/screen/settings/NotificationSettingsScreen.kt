package com.example.livekick.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.livekick.presentation.viewmodel.NotificationSettingsViewModel
import com.example.livekick.presentation.viewmodel.NotificationSettingsViewModelFactory
import com.example.livekick.ui.theme.LocalThemeManager
import com.example.livekick.ui.theme.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = viewModel(
        factory = NotificationSettingsViewModelFactory(LocalContext.current)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val themeManager = LocalThemeManager.current
    val scope = rememberCoroutineScope()
    var showClearDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Настройки",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Секция темы
            NotificationSection(
                title = "Тема приложения",
                icon = Icons.Default.Palette
            ) {
                ThemeSelector(themeManager)
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Секция выбора языка
            NotificationSection(
                title = "Язык интерфейса",
                icon = Icons.Default.Language
            ) {
                LanguageSelector(
                    selected = uiState.language,
                    onSelect = viewModel::setLanguage
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Общие настройки уведомлений
            NotificationSection(
                title = "Общие уведомления",
                icon = Icons.Default.Notifications
            ) {
                SwitchPreference(
                    title = "Включить уведомления",
                    subtitle = "Получать уведомления о матчах",
                    checked = uiState.notificationsEnabled,
                    onCheckedChange = viewModel::setNotificationsEnabled
                )
                
                if (uiState.notificationsEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    SwitchPreference(
                        title = "Уведомления о начале матчей",
                        subtitle = "Получать уведомления за 5 минут до начала",
                        checked = uiState.matchStartNotifications,
                        onCheckedChange = viewModel::setMatchStartNotifications
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    SwitchPreference(
                        title = "Уведомления о событиях",
                        subtitle = "Получать уведомления о голах и карточках",
                        checked = uiState.eventNotifications,
                        onCheckedChange = viewModel::setEventNotifications
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Настройки избранных команд
            NotificationSection(
                title = "Избранные команды",
                icon = Icons.Default.Favorite
            ) {
                SwitchPreference(
                    title = "Уведомления только для избранных",
                    subtitle = "Получать уведомления только о матчах избранных команд",
                    checked = uiState.favoritesOnly,
                    onCheckedChange = viewModel::setFavoritesOnly
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Настройки времени
            NotificationSection(
                title = "Время уведомлений",
                icon = Icons.Default.Info
            ) {
                SwitchPreference(
                    title = "Тихий режим",
                    subtitle = "Не беспокоить в определенное время",
                    checked = uiState.quietModeEnabled,
                    onCheckedChange = viewModel::setQuietModeEnabled
                )
                
                if (uiState.quietModeEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Тихий режим: ${uiState.quietModeStart} - ${uiState.quietModeEnd}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Кнопка тестирования
            Button(
                onClick = viewModel::testNotification,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.notificationsEnabled
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Тест уведомления")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Кнопка очистки кэша
            Button(
                onClick = { showClearDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Очистить кэш и избранное")
            }
            if (showClearDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDialog = false },
                    title = { Text("Очистить все данные?") },
                    text = { Text("Это удалит все матчи и избранное из памяти устройства. Продолжить?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showClearDialog = false
                        }) { Text("Да") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearDialog = false }) { Text("Отмена") }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Статус
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Статус уведомлений",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (uiState.notificationsEnabled) {
                            "✅ Уведомления включены"
                        } else {
                            "❌ Уведомления отключены"
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun SwitchPreference(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun ThemeSelector(themeManager: com.example.livekick.ui.theme.ThemeManager) {
    val current = themeManager.themeMode
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = current == ThemeMode.LIGHT,
                onClick = { themeManager.updateThemeMode(ThemeMode.LIGHT) }
            )
            Text("Светлая", modifier = Modifier.padding(start = 4.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = current == ThemeMode.DARK,
                onClick = { themeManager.updateThemeMode(ThemeMode.DARK) }
            )
            Text("Тёмная", modifier = Modifier.padding(start = 4.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = current == ThemeMode.SYSTEM,
                onClick = { themeManager.updateThemeMode(ThemeMode.SYSTEM) }
            )
            Text("Системная", modifier = Modifier.padding(start = 4.dp))
        }
    }
}

@Composable
private fun LanguageSelector(selected: com.example.livekick.ui.theme.Language, onSelect: (com.example.livekick.ui.theme.Language) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selected == com.example.livekick.ui.theme.Language.RU,
                onClick = { onSelect(com.example.livekick.ui.theme.Language.RU) }
            )
            Text("Русский", modifier = Modifier.padding(start = 4.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(
                selected = selected == com.example.livekick.ui.theme.Language.EN,
                onClick = { onSelect(com.example.livekick.ui.theme.Language.EN) }
            )
            Text("English", modifier = Modifier.padding(start = 4.dp))
        }
    }
} 
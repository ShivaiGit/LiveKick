package com.example.livekick.presentation.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.livekick.domain.model.MatchStatus
import com.example.livekick.presentation.viewmodel.DateFilter
import com.example.livekick.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndFilterBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStatus: MatchStatus?,
    onStatusFilterChange: (MatchStatus?) -> Unit,
    selectedLeague: String?,
    onLeagueFilterChange: (String?) -> Unit,
    selectedDate: DateFilter?,
    onDateFilterChange: (DateFilter?) -> Unit,
    availableLeagues: List<String>,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        // Поисковая строка
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "Поиск по командам...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Поиск",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Очистить",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Кнопка фильтров
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Фильтры",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            IconButton(
                onClick = { isExpanded = !isExpanded }
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Показать фильтры",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Расширенные фильтры
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Фильтр по дате
                FilterSection(
                    title = "Дата",
                    selectedValue = selectedDate?.key,
                    onValueChange = { key ->
                        val filter = DateFilter.values().find { it.key == key }
                        onDateFilterChange(filter)
                    },
                    options = DateFilter.values().map { it.key to it.displayName }
                )
                
                // Фильтр по статусу
                FilterSection(
                    title = "Статус матча",
                    selectedValue = selectedStatus?.name,
                    onValueChange = { value ->
                        onStatusFilterChange(
                            if (value == null) null else MatchStatus.valueOf(value)
                        )
                    },
                    options = listOf(
                        "LIVE" to "В прямом эфире",
                        "SCHEDULED" to "Запланированные",
                        "FINISHED" to "Завершенные"
                    )
                )
                
                // Фильтр по лиге
                FilterSection(
                    title = "Лига",
                    selectedValue = selectedLeague,
                    onValueChange = onLeagueFilterChange,
                    options = availableLeagues.map { it to it }
                )
                
                // Кнопка сброса фильтров
                if (selectedStatus != null || selectedLeague != null || selectedDate != null || searchQuery.isNotEmpty()) {
                    Button(
                        onClick = {
                            onSearchQueryChange("")
                            onStatusFilterChange(null)
                            onLeagueFilterChange(null)
                            onDateFilterChange(null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Сбросить фильтры")
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    selectedValue: String?,
    onValueChange: (String?) -> Unit,
    options: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Опция "Все"
            item {
                FilterChip(
                    text = "Все",
                    selected = selectedValue == null,
                    onClick = { onValueChange(null) }
                )
            }
            
            // Остальные опции
            items(options.size) { index ->
                val (value, label) = options[index]
                FilterChip(
                    text = label,
                    selected = selectedValue == value,
                    onClick = { onValueChange(value) }
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
} 
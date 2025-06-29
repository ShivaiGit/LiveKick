package com.example.livekick.data.repository

import android.content.Context
import android.util.Log
import com.example.livekick.data.local.AppDatabase
import com.example.livekick.data.local.repository.LocalMatchRepository
import com.example.livekick.data.remote.NetworkModule
import com.example.livekick.data.remote.mapper.ApiFootballMapper
import com.example.livekick.domain.model.*
import com.example.livekick.domain.repository.MatchRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MatchRepositoryImpl(
    private val context: Context
) : MatchRepository {
    
    private val apiService = NetworkModule.apiFootballService
    private val localRepository = LocalMatchRepository(AppDatabase.getDatabase(context))
    
    // Функция для тестирования API подключения
    suspend fun testApiConnection() {
        try {
            Log.d("LiveKick", "=== ТЕСТИРОВАНИЕ API ПОДКЛЮЧЕНИЯ ===")
            Log.d("LiveKick", "URL: https://football.sportdevs.com/")
            Log.d("LiveKick", "API Key: iMuRG7tk5kS0bQl-g2z4YQ")
            
            val response = apiService.testApi()
            Log.d("LiveKick", "API тест успешен: ${response}")
            Log.d("LiveKick", "Результат: ${response.size} стран")
            
        } catch (e: Exception) {
            Log.e("LiveKick", "API тест провален: ${e.message}", e)
            Log.e("LiveKick", "Тип ошибки: ${e.javaClass.simpleName}")
            if (e is retrofit2.HttpException) {
                Log.e("LiveKick", "HTTP код: ${e.code()}")
                Log.e("LiveKick", "HTTP сообщение: ${e.message()}")
            }
        }
    }
    
    override fun getLiveMatches(): Flow<List<Match>> = flow {
        while (true) {
            try {
                Log.d("LiveKick", "=== НАЧАЛО ЗАПРОСА LIVE МАТЧЕЙ ===")
                
                // Сначала пробуем получить live матчи
                val liveMatches = mutableListOf<Match>()
                
                try {
                    Log.d("LiveKick", "Запрашиваем live матчи...")
                    val liveResponse = apiService.getLiveMatches()
                    Log.d("LiveKick", "Live матчи ответ получен: ${liveResponse}")
                    Log.d("LiveKick", "Количество матчей: ${liveResponse.size}")
                    
                    if (liveResponse.isNotEmpty()) {
                        Log.d("LiveKick", "Первый матч: ${liveResponse.first()}")
                        val matches = ApiFootballMapper.mapMatchResponseListToMatches(liveResponse)
                        liveMatches.addAll(matches)
                        Log.d("LiveKick", "Live матчи: ${matches.size}")
                    } else {
                        Log.w("LiveKick", "Список live матчей пустой")
                    }
                } catch (e: Exception) {
                    Log.e("LiveKick", "Ошибка получения live матчей: ${e.message}", e)
                    Log.e("LiveKick", "Тип ошибки: ${e.javaClass.simpleName}")
                    if (e is retrofit2.HttpException) {
                        Log.e("LiveKick", "HTTP код: ${e.code()}")
                        Log.e("LiveKick", "HTTP сообщение: ${e.message()}")
                    }
                }
                
                // Если нет live матчей, получаем сегодняшние матчи
                if (liveMatches.isEmpty()) {
                    try {
                        Log.d("LiveKick", "Запрашиваем сегодняшние матчи...")
                        val today = LocalDate.now()
                        val todayResponse = apiService.getMatchesByDate(
                            date = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )
                        if (todayResponse.isNotEmpty()) {
                            val matches = ApiFootballMapper.mapMatchResponseListToMatches(todayResponse)
                            liveMatches.addAll(matches)
                            Log.d("LiveKick", "Сегодняшние матчи: ${matches.size}")
                        }
                    } catch (e: Exception) {
                        Log.e("LiveKick", "Ошибка получения сегодняшних матчей: ${e.message}")
                    }
                }
                
                // Если все еще нет матчей, получаем завтрашние
                if (liveMatches.isEmpty()) {
                    try {
                        Log.d("LiveKick", "Запрашиваем завтрашние матчи...")
                        val tomorrow = LocalDate.now().plusDays(1)
                        val tomorrowResponse = apiService.getMatchesByDate(
                            date = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )
                        if (tomorrowResponse.isNotEmpty()) {
                            val matches = ApiFootballMapper.mapMatchResponseListToMatches(tomorrowResponse)
                            liveMatches.addAll(matches)
                            Log.d("LiveKick", "Завтрашние матчи: ${matches.size}")
                        }
                    } catch (e: Exception) {
                        Log.e("LiveKick", "Ошибка получения завтрашних матчей: ${e.message}")
                    }
                }
                
                Log.d("LiveKick", "Всего получено: ${liveMatches.size} матчей")
                
                // Фильтруем и сортируем матчи
                val filteredMatches = liveMatches
                    .sortedBy { it.dateTime }
                    .take(15)
                    .toMutableList()
                
                Log.d("LiveKick", "После фильтрации: ${filteredMatches.size} матчей")
                
                // Если нет реальных матчей, показываем заглушечные данные
                val matchesToShow = if (filteredMatches.isEmpty()) {
                    Log.d("LiveKick", "Нет реальных матчей, показываем заглушечные данные")
                    generateFallbackMatches()
                } else {
                    filteredMatches
                }
                
                // Сохраняем в локальную базу данных
                if (matchesToShow.isNotEmpty()) {
                    localRepository.saveMatches(matchesToShow)
                    Log.d("LiveKick", "Матчи сохранены в локальную БД")
                }
                
                // Добавляем статус избранного к матчам
                val matchesWithFavorites = matchesToShow.map { match ->
                    // Получаем статус избранного из локальной БД
                    val localMatch = localRepository.getMatchById(match.id)
                    match.copy(isFavorite = localMatch?.isFavorite ?: false)
                }
                
                Log.d("LiveKick", "Отправляем ${matchesWithFavorites.size} матчей в UI")
                emit(matchesWithFavorites)
                
                Log.d("LiveKick", "=== КОНЕЦ ЗАПРОСА LIVE МАТЧЕЙ ===")
                
            } catch (e: Exception) {
                Log.e("LiveKick", "=== ОШИБКА API ===")
                Log.e("LiveKick", "Ошибка API: ${e.message}", e)
                Log.e("LiveKick", "Тип ошибки: ${e.javaClass.simpleName}")
                
                // Пытаемся получить данные из локальной БД
                try {
                    Log.d("LiveKick", "Пытаемся получить данные из локальной БД")
                    val localMatches = localRepository.getLiveAndTodayMatches().first()
                    if (localMatches.isNotEmpty()) {
                        Log.d("LiveKick", "Получено ${localMatches.size} матчей из локальной БД")
                        emit(localMatches)
                    } else {
                        Log.d("LiveKick", "Локальная БД пуста, используем заглушечные данные")
                        emit(generateFallbackMatches())
                    }
                } catch (localError: Exception) {
                    Log.e("LiveKick", "Ошибка локальной БД: ${localError.message}")
                    emit(generateFallbackMatches())
                }
            }
            
            delay(60000) // Обновляем каждые 60 секунд
        }
    }
    
    override fun getMatchesByLeague(leagueId: String): Flow<List<Match>> = flow {
        try {
            val response = apiService.getMatchesByLeague(leagueId)
            val matches = if (response.isNotEmpty()) {
                ApiFootballMapper.mapMatchResponseListToMatches(response)
            } else {
                emptyList()
            }
            
            val matchesWithFavorites = matches.map { match ->
                match.copy(isFavorite = false)
            }
            
            emit(matchesWithFavorites)
        } catch (e: Exception) {
            Log.e("LiveKick", "Ошибка получения матчей по лиге: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    override fun getMatchById(matchId: String): Flow<Match?> = flow {
        try {
            val response = apiService.getMatchById(matchId)
            val match = if (response.isNotEmpty()) {
                ApiFootballMapper.mapMatchResponseToMatch(response.first())
            } else {
                null
            }
            
            val matchWithFavorite = match?.copy(isFavorite = false)
            emit(matchWithFavorite)
        } catch (e: Exception) {
            Log.e("LiveKick", "Ошибка получения матча по ID: ${e.message}", e)
            // Пытаемся получить из локальной БД
            val localMatch = localRepository.getMatchById(matchId)
            emit(localMatch)
        }
    }
    
    override fun getFavoriteMatches(): Flow<List<Match>> {
        return localRepository.getFavoriteMatches()
    }
    
    override suspend fun toggleFavorite(matchId: String) {
        try {
            // Получаем текущий статус из локальной БД
            val currentMatch = localRepository.getMatchById(matchId)
            val newFavoriteStatus = !(currentMatch?.isFavorite ?: false)
            
            // Обновляем статус в локальной БД
            localRepository.updateFavoriteStatus(matchId, newFavoriteStatus)
            
            Log.d("LiveKick", "Статус избранного для матча $matchId изменен на: $newFavoriteStatus")
        } catch (e: Exception) {
            Log.e("LiveKick", "Ошибка изменения статуса избранного: ${e.message}", e)
        }
    }
    
    override suspend fun refreshMatches() {
        // Очищаем старые матчи
        localRepository.clearOldMatches()
        delay(1000)
    }
    
    // Заглушечные данные на случай ошибки API
    private fun generateFallbackMatches(): List<Match> {
        Log.d("LiveKick", "Используем реалистичные заглушечные данные")
        
        val teams = listOf(
            Team("1", "Manchester City", "MCI", "https://crests.football-data.org/65.png"),
            Team("2", "Arsenal", "ARS", "https://crests.football-data.org/57.png"),
            Team("3", "Liverpool", "LIV", "https://crests.football-data.org/64.png"),
            Team("4", "Real Madrid", "RMA", "https://crests.football-data.org/86.png"),
            Team("5", "Barcelona", "BAR", "https://crests.football-data.org/81.png"),
            Team("6", "Bayern Munich", "BAY", "https://crests.football-data.org/5.png"),
            Team("7", "PSG", "PSG", "https://crests.football-data.org/524.png"),
            Team("8", "Juventus", "JUV", "https://crests.football-data.org/109.png"),
            Team("9", "AC Milan", "MIL", "https://crests.football-data.org/98.png"),
            Team("10", "Inter", "INT", "https://crests.football-data.org/108.png"),
            Team("11", "Chelsea", "CHE", "https://crests.football-data.org/61.png"),
            Team("12", "Manchester United", "MUN", "https://crests.football-data.org/66.png")
        )
        
        val leagues = listOf(
            League("2021", "Premier League", "England", "https://crests.football-data.org/PL.png"),
            League("2014", "La Liga", "Spain", "https://crests.football-data.org/laliga.png"),
            League("2002", "Bundesliga", "Germany", "https://crests.football-data.org/BL1.png"),
            League("2019", "Serie A", "Italy", "https://crests.football-data.org/SA.png"),
            League("2015", "Ligue 1", "France", "https://crests.football-data.org/FL1.png"),
            League("2001", "Champions League", "Europe", "https://crests.football-data.org/CL.png")
        )
        
        val now = LocalDateTime.now()
        
        return listOf(
            // Live матчи
            Match(
                id = "live1",
                homeTeam = teams[0], // Man City
                awayTeam = teams[1], // Arsenal
                homeScore = 2,
                awayScore = 1,
                status = MatchStatus.LIVE,
                minute = 67,
                league = leagues[0], // Premier League
                dateTime = now.minusMinutes(67),
                events = listOf(
                    MatchEvent("1", EventType.GOAL, 23, teams[0], "Haaland", "Гол! Хааланд открывает счет"),
                    MatchEvent("2", EventType.GOAL, 45, teams[1], "Saka", "Гол! Сака сравнивает счет"),
                    MatchEvent("3", EventType.GOAL, 52, teams[0], "De Bruyne", "Гол! Де Брюйне выводит вперед"),
                    MatchEvent("4", EventType.YELLOW_CARD, 34, teams[1], "Partey", "Желтая карточка")
                ),
                isFavorite = false
            ),
            
            Match(
                id = "live2",
                homeTeam = teams[3], // Real Madrid
                awayTeam = teams[4], // Barcelona
                homeScore = 0,
                awayScore = 0,
                status = MatchStatus.LIVE,
                minute = 23,
                league = leagues[1], // La Liga
                dateTime = now.minusMinutes(23),
                events = listOf(
                    MatchEvent("5", EventType.YELLOW_CARD, 12, teams[3], "Modric", "Желтая карточка"),
                    MatchEvent("6", EventType.CORNER, 18, teams[4], null, "Угловой")
                ),
                isFavorite = false
            ),
            
            // Сегодняшние матчи
            Match(
                id = "today1",
                homeTeam = teams[2], // Liverpool
                awayTeam = teams[11], // Man United
                homeScore = 0,
                awayScore = 0,
                status = MatchStatus.SCHEDULED,
                minute = null,
                league = leagues[0], // Premier League
                dateTime = now.plusHours(2),
                events = emptyList(),
                isFavorite = false
            ),
            
            Match(
                id = "today2",
                homeTeam = teams[5], // Bayern
                awayTeam = teams[6], // PSG
                homeScore = 0,
                awayScore = 0,
                status = MatchStatus.SCHEDULED,
                minute = null,
                league = leagues[5], // Champions League
                dateTime = now.plusHours(4),
                events = emptyList(),
                isFavorite = false
            ),
            
            Match(
                id = "today3",
                homeTeam = teams[7], // Juventus
                awayTeam = teams[8], // AC Milan
                homeScore = 0,
                awayScore = 0,
                status = MatchStatus.SCHEDULED,
                minute = null,
                league = leagues[3], // Serie A
                dateTime = now.plusHours(6),
                events = emptyList(),
                isFavorite = false
            ),
            
            // Завтрашние матчи
            Match(
                id = "tomorrow1",
                homeTeam = teams[10], // Inter
                awayTeam = teams[9], // AC Milan
                homeScore = 0,
                awayScore = 0,
                status = MatchStatus.SCHEDULED,
                minute = null,
                league = leagues[3], // Serie A
                dateTime = now.plusDays(1).plusHours(3),
                events = emptyList(),
                isFavorite = false
            ),
            
            Match(
                id = "tomorrow2",
                homeTeam = teams[0], // Man City
                awayTeam = teams[2], // Liverpool
                homeScore = 0,
                awayScore = 0,
                status = MatchStatus.SCHEDULED,
                minute = null,
                league = leagues[0], // Premier League
                dateTime = now.plusDays(1).plusHours(5),
                events = emptyList(),
                isFavorite = false
            )
        )
    }
} 
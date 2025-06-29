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
    
    override fun getLiveMatches(): Flow<List<Match>> = flow {
        while (true) {
            try {
                Log.d("LiveKick", "=== НАЧАЛО ЗАПРОСА LIVE МАТЧЕЙ ===")
                Log.d("LiveKick", "Запрашиваем матчи из нескольких лиг")
                
                // Получаем матчи из нескольких популярных лиг
                val allMatches = mutableListOf<Match>()
                
                // Premier League (2021) - только один запрос для экономии лимитов
                try {
                    Log.d("LiveKick", "Запрашиваем Premier League...")
                    val premierResponse = apiService.getMatchesByLeague(leagueId = "2021")
                    premierResponse.matches?.let { matchList ->
                        val matches = ApiFootballMapper.mapMatchResponseListToMatches(matchList)
                        allMatches.addAll(matches)
                        Log.d("LiveKick", "Premier League: ${matches.size} матчей")
                    }
                    
                    // Ждем 2 секунды перед следующим запросом
                    delay(2000)
                    
                } catch (e: Exception) {
                    Log.e("LiveKick", "Ошибка Premier League: ${e.message}")
                }
                
                // Если Premier League не дал результатов, пробуем La Liga
                if (allMatches.isEmpty()) {
                    try {
                        Log.d("LiveKick", "Запрашиваем La Liga...")
                        val laligaResponse = apiService.getMatchesByLeague(leagueId = "2014")
                        laligaResponse.matches?.let { matchList ->
                            val matches = ApiFootballMapper.mapMatchResponseListToMatches(matchList)
                            allMatches.addAll(matches)
                            Log.d("LiveKick", "La Liga: ${matches.size} матчей")
                        }
                        
                        delay(2000)
                        
                    } catch (e: Exception) {
                        Log.e("LiveKick", "Ошибка La Liga: ${e.message}")
                    }
                }
                
                // Если все еще нет матчей, пробуем Bundesliga
                if (allMatches.isEmpty()) {
                    try {
                        Log.d("LiveKick", "Запрашиваем Bundesliga...")
                        val bundesligaResponse = apiService.getMatchesByLeague(leagueId = "2002")
                        bundesligaResponse.matches?.let { matchList ->
                            val matches = ApiFootballMapper.mapMatchResponseListToMatches(matchList)
                            allMatches.addAll(matches)
                            Log.d("LiveKick", "Bundesliga: ${matches.size} матчей")
                        }
                        
                        delay(2000)
                        
                    } catch (e: Exception) {
                        Log.e("LiveKick", "Ошибка Bundesliga: ${e.message}")
                    }
                }
                
                Log.d("LiveKick", "Всего получено: ${allMatches.size} матчей")
                
                // Фильтруем live, сегодняшние и завтрашние матчи
                val today = LocalDate.now()
                val tomorrow = today.plusDays(1)
                
                val filteredMatches = allMatches.filter { match ->
                    match.status == MatchStatus.LIVE ||
                    (match.dateTime.toLocalDate() == today && match.status == MatchStatus.SCHEDULED) ||
                    (match.dateTime.toLocalDate() == tomorrow && match.status == MatchStatus.SCHEDULED)
                }.sortedBy { it.dateTime }.take(15).toMutableList()
                
                Log.d("LiveKick", "После фильтрации: ${filteredMatches.size} матчей")
                
                // Если нет матчей из лиг, пробуем получить матчи по датам (только один запрос)
                if (filteredMatches.isEmpty()) {
                    Log.d("LiveKick", "Нет матчей из лиг, пробуем получить матчи по датам")
                    
                    // Сегодняшние матчи
                    try {
                        val todayResponse = apiService.getMatchesByDate(
                            dateFrom = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            dateTo = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        )
                        todayResponse.matches?.let { matchList ->
                            val matches = ApiFootballMapper.mapMatchResponseListToMatches(matchList)
                            filteredMatches.addAll(matches)
                            Log.d("LiveKick", "Сегодняшние матчи: ${matches.size}")
                        }
                    } catch (e: Exception) {
                        Log.e("LiveKick", "Ошибка получения сегодняшних матчей: ${e.message}")
                    }
                }
                
                val finalMatches = filteredMatches.sortedBy { it.dateTime }.take(15)
                Log.d("LiveKick", "Финальный список: ${finalMatches.size} матчей")
                
                // Если нет реальных матчей, показываем заглушечные данные
                val matchesToShow = if (finalMatches.isEmpty()) {
                    Log.d("LiveKick", "Нет реальных матчей, показываем заглушечные данные")
                    generateFallbackMatches()
                } else {
                    finalMatches
                }
                
                // Сохраняем в локальную базу данных
                if (matchesToShow.isNotEmpty()) {
                    localRepository.saveMatches(matchesToShow)
                    Log.d("LiveKick", "Матчи сохранены в локальную БД")
                }
                
                // Добавляем статус избранного к матчам
                val matchesWithFavorites = matchesToShow.map { match ->
                    match.copy(isFavorite = false) // Будет обновлено из локальной БД
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
            
            delay(60000) // Обновляем каждые 60 секунд (вместо 30)
        }
    }
    
    override fun getMatchesByLeague(leagueId: String): Flow<List<Match>> = flow {
        try {
            val response = apiService.getMatchesByLeague(leagueId)
            val matches = response.matches?.let { matchList ->
                ApiFootballMapper.mapMatchResponseListToMatches(matchList)
            } ?: emptyList()
            
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
            val match = response.matches?.firstOrNull()?.let { matchResponse ->
                ApiFootballMapper.mapMatchResponseToMatch(matchResponse)
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
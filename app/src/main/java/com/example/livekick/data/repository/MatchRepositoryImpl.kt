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
                Log.d("LiveKick", "Запрашиваем матчи из нескольких лиг")
                
                // Получаем матчи из нескольких популярных лиг
                val allMatches = mutableListOf<Match>()
                
                // Premier League (2021)
                try {
                    val premierResponse = apiService.getMatchesByCompetition(competitionId = "2021")
                    premierResponse.matches?.let { matchList ->
                        val matches = ApiFootballMapper.mapMatchResponseListToMatches(matchList)
                        allMatches.addAll(matches)
                        Log.d("LiveKick", "Premier League: ${matches.size} матчей")
                    }
                } catch (e: Exception) {
                    Log.e("LiveKick", "Ошибка Premier League: ${e.message}")
                }
                
                // La Liga (2014)
                try {
                    val laligaResponse = apiService.getMatchesByCompetition(competitionId = "2014")
                    laligaResponse.matches?.let { matchList ->
                        val matches = ApiFootballMapper.mapMatchResponseListToMatches(matchList)
                        allMatches.addAll(matches)
                        Log.d("LiveKick", "La Liga: ${matches.size} матчей")
                    }
                } catch (e: Exception) {
                    Log.e("LiveKick", "Ошибка La Liga: ${e.message}")
                }
                
                // Bundesliga (2002)
                try {
                    val bundesligaResponse = apiService.getMatchesByCompetition(competitionId = "2002")
                    bundesligaResponse.matches?.let { matchList ->
                        val matches = ApiFootballMapper.mapMatchResponseListToMatches(matchList)
                        allMatches.addAll(matches)
                        Log.d("LiveKick", "Bundesliga: ${matches.size} матчей")
                    }
                } catch (e: Exception) {
                    Log.e("LiveKick", "Ошибка Bundesliga: ${e.message}")
                }
                
                Log.d("LiveKick", "Всего получено: ${allMatches.size} матчей")
                
                // Фильтруем только live и ближайшие матчи (сегодняшние)
                val filteredMatches = allMatches.filter { match ->
                    match.status == MatchStatus.LIVE ||
                    (match.dateTime.toLocalDate() == LocalDate.now() && match.status == MatchStatus.SCHEDULED)
                }.take(10) // Ограничиваем до 10 матчей
                
                Log.d("LiveKick", "После фильтрации: ${filteredMatches.size} матчей")
                
                // Сохраняем в локальную базу данных
                if (filteredMatches.isNotEmpty()) {
                    localRepository.saveMatches(filteredMatches)
                    Log.d("LiveKick", "Матчи сохранены в локальную БД")
                }
                
                // Добавляем статус избранного к матчам
                val matchesWithFavorites = filteredMatches.map { match ->
                    match.copy(isFavorite = false) // Будет обновлено из локальной БД
                }
                
                Log.d("LiveKick", "Отправляем ${matchesWithFavorites.size} матчей в UI")
                emit(matchesWithFavorites)
                
            } catch (e: Exception) {
                Log.e("LiveKick", "Ошибка API: ${e.message}", e)
                
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
            
            delay(30000) // Обновляем каждые 30 секунд
        }
    }
    
    override fun getMatchesByLeague(leagueId: String): Flow<List<Match>> = flow {
        try {
            val response = apiService.getMatchesByCompetition(competitionId = leagueId)
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
        Log.d("LiveKick", "Используем заглушечные данные")
        val teams = listOf(
            Team("1", "Реал Мадрид", "РМ", "https://example.com/real.png"),
            Team("2", "Барселона", "БАР", "https://example.com/barca.png"),
            Team("3", "Манчестер Юнайтед", "МЮ", "https://example.com/united.png"),
            Team("4", "Ливерпуль", "ЛИВ", "https://example.com/liverpool.png"),
            Team("5", "Бавария", "БАВ", "https://example.com/bayern.png"),
            Team("6", "ПСЖ", "ПСЖ", "https://example.com/psg.png"),
            Team("7", "Ювентус", "ЮВЕ", "https://example.com/juventus.png"),
            Team("8", "Милан", "МИЛ", "https://example.com/milan.png")
        )
        
        val leagues = listOf(
            League("1", "La Liga", "Испания", "https://example.com/laliga.png"),
            League("2", "Premier League", "Англия", "https://example.com/premier.png"),
            League("3", "Bundesliga", "Германия", "https://example.com/bundesliga.png"),
            League("4", "Serie A", "Италия", "https://example.com/seriea.png")
        )
        
        return listOf(
            Match(
                id = "1",
                homeTeam = teams[0],
                awayTeam = teams[1],
                homeScore = 2,
                awayScore = 1,
                status = MatchStatus.LIVE,
                minute = 67,
                league = leagues[0],
                dateTime = LocalDateTime.now().minusMinutes(67),
                events = listOf(
                    MatchEvent("1", EventType.GOAL, 15, teams[0], "Бензема"),
                    MatchEvent("2", EventType.GOAL, 32, teams[1], "Левандовски"),
                    MatchEvent("3", EventType.GOAL, 58, teams[0], "Винни"),
                    MatchEvent("4", EventType.YELLOW_CARD, 45, teams[1], "Бускетс")
                ),
                isFavorite = false
            ),
            Match(
                id = "2",
                homeTeam = teams[2],
                awayTeam = teams[3],
                homeScore = 0,
                awayScore = 0,
                status = MatchStatus.LIVE,
                minute = 23,
                league = leagues[1],
                dateTime = LocalDateTime.now().minusMinutes(23),
                events = listOf(
                    MatchEvent("5", EventType.YELLOW_CARD, 12, teams[2], "Фернандеш")
                ),
                isFavorite = false
            ),
            Match(
                id = "3",
                homeTeam = teams[4],
                awayTeam = teams[5],
                homeScore = 3,
                awayScore = 2,
                status = MatchStatus.LIVE,
                minute = 89,
                league = leagues[2],
                dateTime = LocalDateTime.now().minusMinutes(89),
                events = listOf(
                    MatchEvent("6", EventType.GOAL, 8, teams[4], "Кейн"),
                    MatchEvent("7", EventType.GOAL, 22, teams[5], "Мбаппе"),
                    MatchEvent("8", EventType.GOAL, 45, teams[4], "Сане"),
                    MatchEvent("9", EventType.GOAL, 67, teams[5], "Неймар"),
                    MatchEvent("10", EventType.GOAL, 78, teams[4], "Кейн")
                ),
                isFavorite = false
            ),
            Match(
                id = "4",
                homeTeam = teams[6],
                awayTeam = teams[7],
                homeScore = 1,
                awayScore = 1,
                status = MatchStatus.LIVE,
                minute = 34,
                league = leagues[3],
                dateTime = LocalDateTime.now().minusMinutes(34),
                events = listOf(
                    MatchEvent("11", EventType.GOAL, 18, teams[6], "Влахович"),
                    MatchEvent("12", EventType.GOAL, 29, teams[7], "Леао")
                ),
                isFavorite = false
            )
        )
    }
} 
package com.example.livekick.data.repository

import android.util.Log
import com.example.livekick.data.remote.NetworkModule
import com.example.livekick.data.remote.mapper.ApiFootballMapper
import com.example.livekick.domain.model.*
import com.example.livekick.domain.repository.MatchRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MatchRepositoryImpl : MatchRepository {
    
    private val favoriteMatches = mutableSetOf<String>()
    private val apiService = NetworkModule.apiFootballService
    
    override fun getLiveMatches(): Flow<List<Match>> = flow {
        while (true) {
            try {
                Log.d("LiveKick", "Запрашиваем живые матчи...")
                val response = apiService.getLiveMatches()
                Log.d("LiveKick", "Получен ответ: ${response.results} матчей")
                
                val matches = response.response?.let { fixtures ->
                    Log.d("LiveKick", "Обрабатываем ${fixtures.size} матчей")
                    ApiFootballMapper.mapFixtureResponseListToMatches(fixtures)
                } ?: emptyList()
                
                // Добавляем статус избранного к матчам
                val matchesWithFavorites = matches.map { match ->
                    match.copy(isFavorite = favoriteMatches.contains(match.id))
                }
                
                Log.d("LiveKick", "Отправляем ${matchesWithFavorites.size} матчей в UI")
                emit(matchesWithFavorites)
            } catch (e: Exception) {
                Log.e("LiveKick", "Ошибка API: ${e.message}", e)
                // В случае ошибки API возвращаем заглушечные данные
                emit(generateFallbackMatches())
            }
            
            delay(30000) // Обновляем каждые 30 секунд
        }
    }
    
    override fun getMatchesByLeague(leagueId: String): Flow<List<Match>> = flow {
        try {
            val response = apiService.getMatchesByLeague(leagueId = leagueId)
            val matches = response.response?.let { fixtures ->
                ApiFootballMapper.mapFixtureResponseListToMatches(fixtures)
            } ?: emptyList()
            
            val matchesWithFavorites = matches.map { match ->
                match.copy(isFavorite = favoriteMatches.contains(match.id))
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
            val match = response.response?.firstOrNull()?.let { fixture ->
                ApiFootballMapper.mapFixtureResponseToMatch(fixture)
            }
            
            val matchWithFavorite = match?.copy(isFavorite = favoriteMatches.contains(matchId))
            emit(matchWithFavorite)
        } catch (e: Exception) {
            Log.e("LiveKick", "Ошибка получения матча по ID: ${e.message}", e)
            emit(null)
        }
    }
    
    override fun getFavoriteMatches(): Flow<List<Match>> = flow {
        try {
            val response = apiService.getLiveMatches()
            val allMatches = response.response?.let { fixtures ->
                ApiFootballMapper.mapFixtureResponseListToMatches(fixtures)
            } ?: emptyList()
            
            val favoriteMatchesList = allMatches.filter { favoriteMatches.contains(it.id) }
                .map { it.copy(isFavorite = true) }
            
            emit(favoriteMatchesList)
        } catch (e: Exception) {
            Log.e("LiveKick", "Ошибка получения избранных матчей: ${e.message}", e)
            emit(emptyList())
        }
    }
    
    override suspend fun toggleFavorite(matchId: String) {
        if (favoriteMatches.contains(matchId)) {
            favoriteMatches.remove(matchId)
        } else {
            favoriteMatches.add(matchId)
        }
    }
    
    override suspend fun refreshMatches() {
        // Обновление происходит автоматически в getLiveMatches()
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
                isFavorite = favoriteMatches.contains("1")
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
                isFavorite = favoriteMatches.contains("2")
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
                isFavorite = favoriteMatches.contains("3")
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
                isFavorite = favoriteMatches.contains("4")
            )
        )
    }
} 
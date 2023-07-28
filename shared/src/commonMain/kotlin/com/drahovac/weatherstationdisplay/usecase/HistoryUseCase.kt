package com.drahovac.weatherstationdisplay.usecase

import com.drahovac.weatherstationdisplay.data.Database
import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.domain.HistoryWeatherDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class HistoryUseCase(
    private val historyWeatherDataRepository: HistoryWeatherDataRepository,
    private val database: Database,
    private val clock: Clock = Clock.System
) {

    val hasData = database.hasData()
    private val _history: MutableStateFlow<List<HistoryObservation>> = MutableStateFlow(emptyList())
    val history = _history.asStateFlow()

    suspend fun fetchHistory(startDate: LocalDate): Result<Any> {
        return historyWeatherDataRepository.fetchHistory(startDate).also {
            it.getOrNull()?.let { data ->
                database.insertHistoryObservations(data)
            }
        }
    }

    suspend fun fetchHistoryUpToDate() {
        database.selectNewestHistoryDate()?.let {
            fetchHistory(it)
            _history.update { initialWeekHistory() }
        }
    }

    private suspend fun initialWeekHistory() = database.selectHistory(
        clock.now().toLocalDateTime(TimeZone.UTC).date.minus(7, DateTimeUnit.DAY),
        clock.now().toLocalDateTime(TimeZone.UTC).date.minus(1, DateTimeUnit.DAY),
    )
}
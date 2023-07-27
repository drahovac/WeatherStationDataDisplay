package com.drahovac.weatherstationdisplay.usecase

import com.drahovac.weatherstationdisplay.data.Database
import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.domain.HistoryWeatherDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

class HistoryUseCase(
    private val historyWeatherDataRepository: HistoryWeatherDataRepository,
    private val database: Database,
) {

    // TODO take last 7 items
    var history: Flow<List<HistoryObservation>> = database.selectHistory(
        Clock.System.now().toLocalDateTime(TimeZone.UTC).date.minus(7, DateTimeUnit.DAY),
        Clock.System.now().toLocalDateTime(TimeZone.UTC).date,
    )

    suspend fun fetchHistory(startDate: LocalDate): Result<Any> {
        return historyWeatherDataRepository.fetchHistory(startDate).also {
            it.getOrNull()?.let { data ->
                database.insertHistoryObservations(data)
            }
        }
    }
}
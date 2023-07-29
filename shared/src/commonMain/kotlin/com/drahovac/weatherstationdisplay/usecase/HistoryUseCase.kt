package com.drahovac.weatherstationdisplay.usecase

import com.drahovac.weatherstationdisplay.data.Database
import com.drahovac.weatherstationdisplay.domain.HistoryWeatherDataRepository
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

    suspend fun fetchHistory(startDate: LocalDate): Result<Any> {
        // TODO split by months - max 1 month per request
        return historyWeatherDataRepository.fetchHistory(startDate).also {
            it.getOrNull()?.let { data ->
                database.insertHistoryObservations(data)
            }
        }
    }

    suspend fun fetchHistoryUpToDate() {
        database.selectNewestHistoryDate()?.let {
            fetchHistory(it)
        }
    }

    suspend fun getWeekHistory() = database.selectHistory(
        clock.now().toLocalDateTime(TimeZone.UTC).date.minus(7, DateTimeUnit.DAY),
        clock.now().toLocalDateTime(TimeZone.UTC).date.minus(1, DateTimeUnit.DAY),
    )

    suspend fun getMonthHistory() = database.selectHistory(
        clock.now().toLocalDateTime(TimeZone.UTC).date.minus(1, DateTimeUnit.MONTH),
        clock.now().toLocalDateTime(TimeZone.UTC).date.minus(1, DateTimeUnit.DAY),
    )

    suspend fun getYesterdayHistory() = database.selectHistory(
        clock.now().toLocalDateTime(TimeZone.UTC).date.minus(2, DateTimeUnit.DAY),
        clock.now().toLocalDateTime(TimeZone.UTC).date.minus(1, DateTimeUnit.DAY),
    )
}
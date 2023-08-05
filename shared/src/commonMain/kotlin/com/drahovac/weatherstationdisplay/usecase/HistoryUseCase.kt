package com.drahovac.weatherstationdisplay.usecase

import com.drahovac.weatherstationdisplay.data.Database
import com.drahovac.weatherstationdisplay.domain.HistoryWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.toFormattedDate
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class HistoryUseCase(
    private val historyWeatherDataRepository: HistoryWeatherDataRepository,
    private val database: Database,
    private val clock: Clock = Clock.System
) {

    val hasData = database.hasData()

    suspend fun fetchHistory(startDate: LocalDate): Result<Any> {
        val dateNow = clock.now().toLocalDateTime(TimeZone.UTC).date
        val startDates = mutableListOf(startDate)
        while (startDates.last().plusMonth() <= dateNow) {
            startDates.add(startDates.last().plus(1, DateTimeUnit.MONTH))
        }

        return runCatching {
            startDates.mapIndexed { index, localDate ->
                val endDate = startDates.getOrNull(index + 1)
                val fetchRes = endDate?.let {
                    historyWeatherDataRepository.fetchHistory(localDate, endDate)
                } ?: run { historyWeatherDataRepository.fetchHistory(localDate) }
                database.insertHistoryObservations(fetchRes.getOrThrow())
            }
        }
    }

    private fun LocalDate.plusMonth() = plus(1, DateTimeUnit.MONTH)

    suspend fun fetchHistoryUpToDate() {
        database.selectNewestHistoryDate()?.let {
            if (it != clock.now().toLocalDateTime(TimeZone.UTC).date) fetchHistory(it)
        }
    }

    suspend fun getWeekHistory() = database.selectHistory(
        clock.now().toLocalDateTime(TimeZone.UTC).date.minus(8, DateTimeUnit.DAY),
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
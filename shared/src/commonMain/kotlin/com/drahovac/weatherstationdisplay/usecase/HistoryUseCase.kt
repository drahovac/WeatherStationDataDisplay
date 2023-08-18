package com.drahovac.weatherstationdisplay.usecase

import com.drahovac.weatherstationdisplay.data.Database
import com.drahovac.weatherstationdisplay.domain.History
import com.drahovac.weatherstationdisplay.domain.HistoryWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.firstDayOfMonth
import com.drahovac.weatherstationdisplay.domain.firstDayOfWeek
import com.drahovac.weatherstationdisplay.domain.lastDayOfMonth
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

    suspend fun getWeekHistory(startDate: LocalDate? = null): History {
        val firstDay = startDate?.firstDayOfWeek() ?: clock.firstDayOfWeek()
        return History(
            firstDay,
            firstDay.plus(1, DateTimeUnit.WEEK).minus(1,DateTimeUnit.DAY),
            database.selectHistory(
                firstDay,
                firstDay.plus(1, DateTimeUnit.WEEK).minus(1,DateTimeUnit.DAY),
            )
        )
    }

    suspend fun getMonthHistory(
        startDate: LocalDate? = null
    ): History {
        val firstOfMonth =
            (startDate ?: clock.now().toLocalDateTime(TimeZone.UTC).date).firstDayOfMonth()
        return History(
            firstOfMonth,
            firstOfMonth.lastDayOfMonth(),
            database.selectHistory(
                firstOfMonth,
                firstOfMonth.lastDayOfMonth(),
            )
        )
    }

    suspend fun getYesterdayHistory(): History {
        val start = clock.now().toLocalDateTime(TimeZone.UTC).date.minus(1, DateTimeUnit.DAY)
        return History(
            start,
            start,
            database.selectHistory(
                start, start
            )
        )
    }
}
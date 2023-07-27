package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.domain.HistoryWeatherDataRepository
import io.ktor.util.reflect.typeInfo
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class HistoryWeatherDataRepositoryImpl(
    private val networkClient: NetworkClient,
) : HistoryWeatherDataRepository {

    override suspend fun fetchHistory(startDate: LocalDate): Result<List<HistoryObservation>> {
        return networkClient.request<HistoryWeatherDto>(
            path = "v2/pws/history/daily",
            params = mapOf(
                "units" to "m", // TODO settings for units
                "numericPrecision" to "decimal",
                "format" to "json",
                "startDate" to startDate.toServerString(),
                "endDate" to Clock.System.now().toLocalDateTime(TimeZone.UTC).date.toServerString()
            ),
            typeInfo<HistoryWeatherDto>()
        ).map { it.observations }
    }
}

private fun LocalDate.toServerString(): String {
    return toString().replace("-", "")
}

package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.domain.Forecast
import com.drahovac.weatherstationdisplay.domain.ForecastRepository
import io.ktor.util.reflect.typeInfo

class ForecastRepositoryImpl(
    private val networkClient: NetworkClient,
) : ForecastRepository {
    override suspend fun fetchForecast(language: String, geoCode: String): Result<Forecast> {
        return networkClient.request(
            path = "v3/wx/forecast/daily/5day",
            params = mapOf(
                "units" to "m", // TODO settings for units
                "format" to "json",
                "language" to language,
                "geocode" to geoCode
            ),
            typeInfo<Forecast>(),
            skipStationID = true
        )
    }
}

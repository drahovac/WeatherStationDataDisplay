package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.domain.CurrentWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.CurrentWeatherObservation
import io.ktor.util.reflect.typeInfo

class CurrentWeatherDataRepositoryImpl(
    private val networkClient: NetworkClient,
) : CurrentWeatherDataRepository {

    override suspend fun getCurrentData(): Result<CurrentWeatherObservation> {
        return networkClient.request<CurrentWeatherDto>(
            path = "v2/pws/observations/current",
            params = mapOf(
                "units" to "m", // TODO settings for units
                "numericPrecision" to "decimal",
                "format" to "json"
            ),
            typeInfo<CurrentWeatherDto>()
        ).map { it.observations.first() }
    }
}


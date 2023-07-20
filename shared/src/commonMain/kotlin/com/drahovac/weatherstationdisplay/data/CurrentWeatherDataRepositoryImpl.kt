package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.domain.CurrentWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.CurrentWeatherObservation
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.drahovac.weatherstationdisplay.domain.NetworkError
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CurrentWeatherDataRepositoryImpl(
    private val networkClient: NetworkClient,
    private val deviceCredentialsRepository: DeviceCredentialsRepository
) : CurrentWeatherDataRepository {

    override suspend fun getCurrentData(): Result<CurrentWeatherObservation> {
        return withContext(Dispatchers.IO) {
            val deviceId = deviceCredentialsRepository.getDeviceId()
            val apiKey = deviceCredentialsRepository.getApiKey()

            when {
                deviceId == null -> Result.failure(NetworkError.InvalidDeviceId)
                apiKey == null -> Result.failure(NetworkError.InvalidApiKey)
                else -> {
                    runCatching {
                        val dto = networkClient.request(
                            path = "v2/pws/observations/current",
                            params = mapOf(
                                "stationId" to deviceId,
                                "apiKey" to apiKey,
                                "units" to "m", // TODO settings for units
                                "numericPrecision" to "decimal",
                                "format" to "json"
                            )
                        ).call.body(typeInfo<CurrentWeatherDto>()) as CurrentWeatherDto
                        Result.success(dto.observations.first())
                    }.getOrElse {
                        Result.failure(parseError(it))
                    }
                }
            }
        }
    }

    // TODO parse error
    private fun parseError(error: Throwable): NetworkError {
        return when {
            error.message.orEmpty().contains("stationID") -> NetworkError.InvalidDeviceId
            error.message.orEmpty().contains("Invalid apiKey.") -> NetworkError.InvalidApiKey
            else -> NetworkError.General(error)
        }
    }
}

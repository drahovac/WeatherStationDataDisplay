package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.domain.CurrentWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.CurrentWeatherObservation
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.drahovac.weatherstationdisplay.domain.NetworkError
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.JsonConvertException
import io.ktor.util.reflect.typeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CurrentWeatherDataRepositoryImpl(
    private val networkClient: NetworkClient,
    private val deviceCredentialsRepository: DeviceCredentialsRepository
) : CurrentWeatherDataRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getCurrentData(): Result<CurrentWeatherObservation> {
        return withContext(Dispatchers.IO) {
            val deviceId = deviceCredentialsRepository.getDeviceId()
            val apiKey = deviceCredentialsRepository.getApiKey()

            when {
                deviceId == null -> Result.failure(NetworkError.InvalidDeviceId)
                apiKey == null -> Result.failure(NetworkError.InvalidApiKey)
                else -> {
                    runCatching {
                        networkClient.request<CurrentWeatherDto>(
                            path = "v2/pws/observations/current",
                            params = mapOf(
                                "stationId" to deviceId,
                                "apiKey" to apiKey,
                                "units" to "m", // TODO settings for units
                                "numericPrecision" to "decimal",
                                "format" to "json"
                            ),
                            typeInfo<CurrentWeatherDto>()
                        ).map { it.observations.first() }
                    }.getOrElse { error ->
                        when (error) {
                            is ClientRequestException -> Result.failure(error.parseErrorBody())
                            is JsonConvertException -> Result.failure(NetworkError.InvalidDeviceId)
                            else -> Result.failure(NetworkError.General(error))
                        }
                    }
                }
            }
        }
    }

    private suspend fun ClientRequestException.parseErrorBody(): NetworkError {
        return runCatching {
            this.response.bodyAsText().let {
                json.decodeFromString<ErrorResponse>(it)
            }.getNetworkError()
        }.getOrElse {
            return NetworkError.General(this)
        }
    }
}

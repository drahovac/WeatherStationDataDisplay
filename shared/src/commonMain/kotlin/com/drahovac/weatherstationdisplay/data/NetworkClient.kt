package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.drahovac.weatherstationdisplay.domain.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.JsonConvertException
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.TypeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class NetworkClient(
    private val deviceCredentialsRepository: DeviceCredentialsRepository
) {
    private val client = HttpClient { clientConfiguration() }

    suspend fun <T> request(
        path: String,
        params: Map<String, String>,
        typeInfo: TypeInfo,
        skipStationID: Boolean = false,
    ): Result<T> {
        return withContext(Dispatchers.IO) {
            val deviceId = deviceCredentialsRepository.getDeviceId()
            val apiKey = deviceCredentialsRepository.getApiKey()
            val securedParams = securedParams(deviceId, apiKey, skipStationID).apply {
                putAll(params)
            }

            return@withContext when {
                deviceId == null -> Result.failure(NetworkError.InvalidDeviceId)
                apiKey == null -> Result.failure(NetworkError.InvalidApiKey)
                else -> {
                    runCatching {
                        Result.success(client.get {
                            url {
                                protocol = URLProtocol.HTTPS
                                host = "api.weather.com"
                                path(path)
                                securedParams.forEach {
                                    parameters.append(it.key, it.value)
                                }
                            }
                        }.call.let {
                            it.body(typeInfo) as T
                        })
                    }.getOrElse { error ->
                        getNetworkError(error)
                    }
                }
            }
        }
    }

    private fun securedParams(
        deviceId: String?,
        apiKey: String?,
        skipStationID: Boolean
    ) = if (skipStationID) {
        mutableMapOf(
            "apiKey" to apiKey.orEmpty(),
        )
    } else {
        mutableMapOf(
            "stationId" to deviceId.orEmpty(),
            "apiKey" to apiKey.orEmpty(),
        )
    }

    private fun HttpClientConfig<*>.clientConfiguration() {
        expectSuccess = true
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    co.touchlab.kermit.Logger.d(message)
                }
            }
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            socketTimeoutMillis = 15000
        }
        install(ContentNegotiation) {
            json(json)
        }
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        internal val json = Json {
            encodeDefaults = true
            explicitNulls = false
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }

        suspend fun <T> getNetworkError(error: Throwable): Result<T> = when (error) {
            is ClientRequestException -> Result.failure(error.parseErrorBody())
            is JsonConvertException -> Result.failure(NetworkError.InvalidDeviceId)
            else -> Result.failure(NetworkError.General(error))
        }

        private suspend fun ClientRequestException.parseErrorBody(): NetworkError {
            return runCatching {
                this.response.bodyAsText().let {
                    com.drahovac.weatherstationdisplay.data.json.decodeFromString<ErrorResponse>(
                        it
                    )
                }.getNetworkError()
            }.getOrElse {
                return NetworkError.General(this)
            }
        }
    }
}
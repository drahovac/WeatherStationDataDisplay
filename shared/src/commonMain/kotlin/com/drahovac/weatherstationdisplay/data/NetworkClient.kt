package com.drahovac.weatherstationdisplay.data

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.reflect.TypeInfo
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class NetworkClient {

    private val client = HttpClient { clientConfiguration() }

    suspend fun <T> request(
        path: String,
        params: Map<String, String>,
        typeInfo: TypeInfo
    ): Result<T> {
        val dto = client.get {
            url {
                protocol = URLProtocol.HTTPS
                host = "api.weather.com"
                path(path)
                params.forEach {
                    parameters.append(it.key, it.value)
                }
            }
        }.call.let {
            it.body(typeInfo) as T
        }
        return Result.success(dto)
    }

    private fun HttpClientConfig<*>.clientConfiguration() {
        expectSuccess = true
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    // TODO logs
                    println(message)
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
    }
}
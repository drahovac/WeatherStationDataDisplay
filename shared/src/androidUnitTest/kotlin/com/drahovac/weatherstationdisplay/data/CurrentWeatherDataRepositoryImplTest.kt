package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.drahovac.weatherstationdisplay.domain.NetworkError
import io.ktor.client.call.HttpClientCall
import io.ktor.client.statement.DefaultHttpResponse
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.util.InternalAPI
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.coroutines.CoroutineContext
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class CurrentWeatherDataRepositoryImplTest {

    private val networkClient: NetworkClient = mockk()
    private val deviceCredentialsRepository: DeviceCredentialsRepository = mockk()
    private val currentWeatherDataRepositoryImpl = CurrentWeatherDataRepositoryImpl(
        networkClient, deviceCredentialsRepository
    )

    @Test
    fun `return device id error if missing in repository`() = runTest {
        coEvery { deviceCredentialsRepository.getDeviceId() } returns null
        coEvery { deviceCredentialsRepository.getApiKey() } returns "api"

        assertEquals(
            NetworkError.InvalidDeviceId,
            currentWeatherDataRepositoryImpl.getCurrentData()
                .exceptionOrNull()
        )
    }

    @Test
    fun `return invalid api key error if missing in repository`() = runTest {
        coEvery { deviceCredentialsRepository.getDeviceId() } returns "deviceId"
        coEvery { deviceCredentialsRepository.getApiKey() } returns null

        assertEquals(
            NetworkError.InvalidApiKey,
            currentWeatherDataRepositoryImpl.getCurrentData()
                .exceptionOrNull()
        )
    }

    @Test
    fun `return device id error if mentioned in response error`() = runTest {
        coEvery { deviceCredentialsRepository.getDeviceId() } returns "id"
        coEvery { deviceCredentialsRepository.getApiKey() } returns "api"
        coEvery { networkClient.request(any(), any()) } throws IllegalStateException(
            STATION_ID_ERROR_EXAMPLE
        )

        assertEquals(
            NetworkError.InvalidDeviceId,
            currentWeatherDataRepositoryImpl.getCurrentData()
                .exceptionOrNull()
        )
    }

    @Test
    fun `return api key error if mentioned in response error`() = runTest {
        coEvery { deviceCredentialsRepository.getDeviceId() } returns "id"
        coEvery { deviceCredentialsRepository.getApiKey() } returns "api"
        coEvery { networkClient.request(any(), any()) } throws IllegalStateException(
            API_ERROR_EXAMPLE
        )

        assertEquals(
            NetworkError.InvalidApiKey,
            currentWeatherDataRepositoryImpl.getCurrentData()
                .exceptionOrNull()
        )
    }

    @Test
    fun `return general error if error response`() = runTest {
        coEvery { deviceCredentialsRepository.getDeviceId() } returns "id"
        coEvery { deviceCredentialsRepository.getApiKey() } returns "api"
        coEvery { networkClient.request(any(), any()) } throws IllegalStateException(
            "Unknown"
        )

        assertTrue {
            currentWeatherDataRepositoryImpl.getCurrentData()
                .exceptionOrNull() is NetworkError.General
        }
    }

    @Test
    fun `return data on success response`() = runTest {
        coEvery { deviceCredentialsRepository.getDeviceId() } returns "id"
        coEvery { deviceCredentialsRepository.getApiKey() } returns "api"


        println(currentWeatherDataRepositoryImpl.getCurrentData()
            .exceptionOrNull())
    }

    private companion object {
        const val STATION_ID_ERROR_EXAMPLE =
            "Text: \"{\"metadata\":{\"status_code\":400},\"error\":{\"message\":\"stationID " +
                    "is missed. Please set valid stationID\""
        const val API_ERROR_EXAMPLE = "\"errors\":[{\"error\":{\"code\":\"CDN-0001\"," +
                "\"message\":\"Invalid apiKey.\"}"
        const val SUCCESS_DATA =
            "{\"observations\":[{\"stationID\":\"IPCHER9\",\"obsTimeUtc\":\"2023-07-20T19:25:04Z\"," +
                    "\"obsTimeLocal\":\"2023-07-20 21:25:04\",\"neighborhood\":\"Pchery\"," +
                    "\"softwareType\":null,\"country\":\"CZ\",\"solarRadiation\":0.0," +
                    "\"lon\":14.115,\"realtimeFrequency\":null,\"epoch\":1689881104," +
                    "\"lat\":50.195,\"uv\":0.0,\"winddir\":255,\"humidity\":62.0," +
                    "\"qcStatus\":1,\"metric\":{\"temp\":17.8,\"heatIndex\":17.8," +
                    "\"dewpt\":10.4,\"windChill\":17.8,\"windSpeed\":0.0," +
                    "\"windGust\":0.0,\"pressure\":1013.55,\"precipRate\":0.00," +
                    "\"precipTotal\":0.00,\"elev\":289.0}}]}"
    }
}

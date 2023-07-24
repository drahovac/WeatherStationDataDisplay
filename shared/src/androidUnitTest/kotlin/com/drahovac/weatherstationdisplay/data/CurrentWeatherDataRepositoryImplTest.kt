package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.drahovac.weatherstationdisplay.domain.NetworkError
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
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
    fun `return general error if error response`() = runTest {
        coEvery { deviceCredentialsRepository.getDeviceId() } returns "id"
        coEvery { deviceCredentialsRepository.getApiKey() } returns "api"
        coEvery {
            networkClient.request<CurrentWeatherDto>(
                any(),
                any(),
                any()
            )
        } throws IllegalStateException(
            "Unknown"
        )

        assertTrue {
            currentWeatherDataRepositoryImpl.getCurrentData()
                .exceptionOrNull() is NetworkError.General
        }
    }
}

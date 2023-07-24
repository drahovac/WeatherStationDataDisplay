package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.CurrentWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.CurrentWeatherObservation
import com.drahovac.weatherstationdisplay.domain.Destination
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.drahovac.weatherstationdisplay.domain.Metric
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

internal class CurrentWeatherViewModelTest {

    private val currentWeatherDataRepository: CurrentWeatherDataRepository = mockk()
    private val credentialsRepository: DeviceCredentialsRepository = mockk(relaxUnitFun = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var currentWeatherViewModel: CurrentWeatherViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        coEvery { currentWeatherDataRepository.getCurrentData() } returns Result.success(OBSERVATION)
        Dispatchers.setMain(testDispatcher)
        currentWeatherViewModel =
            CurrentWeatherViewModel(currentWeatherDataRepository, credentialsRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetch data observe weather and than every 15 seconds`() = runTest(testDispatcher) {
        val job = launch { currentWeatherViewModel.observeWeather() }

        advanceTimeBy(30001)

        coVerify(exactly = 3) { currentWeatherDataRepository.getCurrentData() }
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `set state from response`() = runTest(testDispatcher) {
        val job = launch { currentWeatherViewModel.observeWeather() }
        advanceTimeBy(1)

        assertEquals(OBSERVATION, currentWeatherViewModel.state.value.observation)
        job.cancel()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `renew api key`() = runTest(testDispatcher) {
        currentWeatherViewModel.onNewApiKey()

        testDispatcher.scheduler.advanceTimeBy(1)

        coVerify { credentialsRepository.removeApiKey() }
        assertEquals(
            Destination.SetupApiKey,
            currentWeatherViewModel.navigationFlow.value.receive()
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `renew device id`() = runTest(testDispatcher) {
        currentWeatherViewModel.onNewDeviceId()

        testDispatcher.scheduler.advanceTimeBy(1)

        coVerify { credentialsRepository.removeDeviceId() }
        assertEquals(
            Destination.SetupDeviceId,
            currentWeatherViewModel.navigationFlow.value.receive()
        )
    }

    private companion object {
        val OBSERVATION = CurrentWeatherObservation(
            stationID = "KSFO",
            obsTimeUtc = "2023-07-20T12:47:54Z",
            obsTimeLocal = "2023-07-20T19:47:54-07:00",
            neighborhood = "San Francisco",
            softwareType = null,
            country = "United States",
            solarRadiation = 123.45,
            lon = -122.45,
            realtimeFrequency = null,
            epoch = 1658115674,
            lat = 37.77,
            uv = 4.56,
            winddir = 123,
            humidity = 65.78,
            qcStatus = 1,
            metric = Metric(
                temp = 23.45,
                heatIndex = 25.67,
                dewpt = 12.34,
                windChill = 10.0,
                windSpeed = 12.34,
                windGust = 15.67,
                pressure = 1013.25,
                precipRate = 0.0,
                precipTotal = 0.0,
                elev = 100.0
            )
        )
    }
}
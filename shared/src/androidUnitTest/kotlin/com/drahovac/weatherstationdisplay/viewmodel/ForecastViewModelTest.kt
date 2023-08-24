package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.CurrentWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.drahovac.weatherstationdisplay.domain.ForecastRepository
import com.drahovac.weatherstationdisplay.domain.NetworkError
import com.drahovac.weatherstationdisplay.domain.forecastPrototype
import com.drahovac.weatherstationdisplay.viewmodel.CurrentWeatherViewModelTest.Companion.OBSERVATION
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ForecastViewModelTest {
    private val currentWeatherDataRepository: CurrentWeatherDataRepository = mockk()
    private val credentialsRepository: DeviceCredentialsRepository = mockk(relaxUnitFun = true)
    private val forecastRepository: ForecastRepository = mockk(relaxUnitFun = true)
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var forecastViewModel: ForecastViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        coEvery { currentWeatherDataRepository.getCurrentData() } returns Result.success(
            OBSERVATION
        )
        Dispatchers.setMain(testDispatcher)
    }

    private fun initViewModel() {
        forecastViewModel =
            ForecastViewModel(
                currentWeatherDataRepository,
                credentialsRepository,
                forecastRepository
            )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `get station code and fetch it if not present`() = runTest(testDispatcher) {
        coEvery { credentialsRepository.getStationCode() } returns null
        coEvery {
            forecastRepository.fetchForecast(
                "en",
                "37.77,-122.45"
            )
        } returns Result.success(FORECAST)
        initViewModel()
        testDispatcher.scheduler.advanceTimeBy(1)

        coVerify { credentialsRepository.getStationCode() }
        coVerify { currentWeatherDataRepository.getCurrentData() }
        coVerify { credentialsRepository.saveStationCode(OBSERVATION.stationCode) }

        assertNull(forecastViewModel.state.value.error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `set error on observation returns error`() = runTest(testDispatcher) {
        coEvery { credentialsRepository.getStationCode() } returns null
        coEvery { currentWeatherDataRepository.getCurrentData() } returns Result.failure(
            NetworkError.InvalidApiKey
        )
        initViewModel()
        testDispatcher.scheduler.advanceTimeBy(1)

        coVerify { credentialsRepository.getStationCode() }
        coVerify { currentWeatherDataRepository.getCurrentData() }

        assertEquals(NetworkError.InvalidApiKey, forecastViewModel.state.value.error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `set error on fetch forecast error`() = runTest(testDispatcher) {
        coEvery { credentialsRepository.getStationCode() } returns null
        coEvery {
            forecastRepository.fetchForecast(
                "en",
                "37.77,-122.45"
            )
        } returns Result.failure(NetworkError.InvalidApiKey)
        initViewModel()
        testDispatcher.scheduler.advanceTimeBy(1)

        coVerify { credentialsRepository.getStationCode() }
        coVerify { currentWeatherDataRepository.getCurrentData() }
        coVerify { credentialsRepository.saveStationCode(OBSERVATION.stationCode) }

        assertEquals(NetworkError.InvalidApiKey, forecastViewModel.state.value.error)
    }

    @Test
    fun `fetch forecast and map state`() {
        successResponse()

        forecastViewModel.state.value.days.let {
            assertEquals(25, it[0].temperatureMax)
            assertEquals(16, it[0].temperatureMin)
            assertEquals("2022-05-21", it[0].dateTime.date.toString())
            assertEquals(27, it[1].temperatureMax)
            assertEquals(18, it[1].temperatureMin)
            assertEquals("2022-05-22", it[1].dateTime.date.toString())
            assertEquals(28, it[4].temperatureMax)
            assertEquals(19, it[4].temperatureMin)
            assertEquals("2022-05-25", it[4].dateTime.date.toString())
        }
    }

    @Test
    fun `select day index`() {
        successResponse()

        forecastViewModel.selectDay(2)

        assertEquals(2, forecastViewModel.state.value.selectedDayIndex)
        forecastViewModel.state.value.selectedDay?.let {
            assertEquals(28, it.temperatureMax)
            assertEquals(19, it.temperatureMin)
            assertEquals("6, low", it.uvIndex)
            assertEquals("More sun than clouds. High 28C. Winds W at 10 to 15 km/h.", it.narrative)
        }
    }

    @Test
    fun `refresh forecast`() {
        successResponse()

        forecastViewModel.selectDay(3)
        forecastViewModel.refresh()
        testDispatcher.scheduler.advanceTimeBy(1)

        coVerify(exactly = 2) { forecastRepository.fetchForecast(
            "en",
            "geo"
        ) }
        assertEquals(3, forecastViewModel.state.value.selectedDayIndex)
    }

    private fun successResponse() {
        coEvery { credentialsRepository.getStationCode() } returns "geo"
        coEvery {
            forecastRepository.fetchForecast(
                "en",
                "geo"
            )
        } returns Result.success(FORECAST)

        initViewModel()
        testDispatcher.scheduler.advanceTimeBy(1)
    }

    private companion object {
        val FORECAST = forecastPrototype
    }
}

package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.CurrentWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.drahovac.weatherstationdisplay.domain.Forecast
import com.drahovac.weatherstationdisplay.domain.ForecastRepository
import com.drahovac.weatherstationdisplay.domain.Locale
import com.drahovac.weatherstationdisplay.domain.language
import com.drahovac.weatherstationdisplay.domain.networkErrorOrNull
import com.rickclephas.kmm.viewmodel.coroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ForecastViewModel(
    private val currentWeatherDataRepository: CurrentWeatherDataRepository,
    private val credentialsRepository: DeviceCredentialsRepository,
    private val forecastRepository: ForecastRepository,
) : SecuredNavigationViewModel(credentialsRepository), ForecastActions {

    private val _state = MutableStateFlow(ForecastState())

    @NativeCoroutines
    val state = _state.asStateFlow()

    init {
        viewModelScope.coroutineScope.launch {
            var code = credentialsRepository.getStationCode()
            if (code == null) {
                code = fetchStationCode()
            }
            code?.let {
                forecastRepository.fetchForecast(
                    Locale.language(),
                    it
                ).updateState()
            }
        }
    }

    private suspend fun fetchStationCode(): String? {
        val result = currentWeatherDataRepository.getCurrentData()
        result.networkErrorOrNull()?.let { error ->
            _state.update { ForecastState(error = error) }
        }
        return result.getOrNull()?.stationCode?.also { credentialsRepository.saveStationCode(it) }
    }

    private fun Result<Forecast>.updateState() {
        val state = getOrNull()?.toState() ?: ForecastState(error = networkErrorOrNull())
        _state.update { state }
    }

    private fun Forecast.toState(): ForecastState {
        return ForecastState(
            days = List(5) {
                ForecastDayState(
                    dateTime = dateTimes[it].toLocalDateTime(TimeZone.currentSystemDefault()),
                    temperatureMax = calendarDayTemperatureMax[it],
                    temperatureMin = calendarDayTemperatureMin[it],
                    icon = daypart.first().iconCode.firstNotNullOf { it },
                    narrative = narrative.first(),
                )
            }
        )
    }

    override fun selectDay(index: Int) {
        _state.update { it.copy(selectedDayIndex = index) }
    }
}

interface ForecastActions {
    fun selectDay(index: Int)
}

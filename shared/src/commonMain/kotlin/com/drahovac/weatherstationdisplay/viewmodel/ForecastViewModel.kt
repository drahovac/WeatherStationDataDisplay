package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.CurrentWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.Daypart
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
import kotlinx.datetime.Instant
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
            days = List(5) { dayIndex ->
                val dayPartIndexes = listOf(2 * dayIndex, 2 * dayIndex + 1)
                val dayPart = daypart.first()

                ForecastDayState(
                    dateTime = dateTimesUtc[dayIndex].toLocalDateTime(TimeZone.currentSystemDefault()),
                    temperatureMax = calendarDayTemperatureMax[dayIndex],
                    temperatureMin = calendarDayTemperatureMin[dayIndex],
                    snowOutlook = qpfSnow[dayIndex],
                    rainOutlook = qpf[dayIndex],
                    icon = daypart.first().iconCode.firstNotNullOf { it },
                    sunrise = sunrisesUtc[dayIndex].toTimeString(),
                    sunset = sunsetsUtc[dayIndex].toTimeString(),
                    narrative = narrative.first(),
                    moonPhase = moonPhaseCode[dayIndex].toPhase(),
                    moonPhaseDesc = moonPhase[dayIndex],
                    dayParts = dayPartIndexes.mapNotNull { index -> dayPart.toDayPartsState(index) },
                    uvIndex = dayPartIndexes.maxBy { index -> dayPart.uvIndex[index] ?: -1 }
                        .let { index ->
                            "${dayPart.uvIndex[index] ?: 0}, ${
                                dayPart.uvDescription[index].orEmpty()
                            }"
                        },
                )
            }
        )
    }

    private fun Instant?.toTimeString(): String =
        this?.toLocalDateTime(TimeZone.currentSystemDefault())?.time?.toString().orEmpty()

    override fun selectDay(index: Int) {
        _state.update { it.copy(selectedDayIndex = index) }
    }
}

private fun Daypart.toDayPartsState(index: Int): DayPartState? {
    return if (daypartName.getOrNull(index) != null && iconCode.getOrNull(index) != null) {
        DayPartState(
            icon = iconCode[index]!!,
            name = daypartName[index]!!,
            narrative = narrative[index].orEmpty(),
            precipChance = precipChance[index] ?: 0,
            precipDesc = precipType[index] ?: "",
            relativeHumidity = relativeHumidity[index] ?: 0
        )
    } else null
}

private fun String.toPhase(): MoonPhase {
    return when (this) {
        "WNG" -> MoonPhase.WANING_GIBBOUS
        "WXC" -> MoonPhase.WAXING_CRESCENT
        "FQ" -> MoonPhase.FIRST_QUARTER
        "LQ" -> MoonPhase.LAST_QUARTER
        "WXG" -> MoonPhase.WAXING_GIBBOUS
        "N" -> MoonPhase.NEW
        "WNC" -> MoonPhase.WANING_CRESCENT
        "F" -> MoonPhase.FULL
        else -> MoonPhase.NEW
    }
}

interface ForecastActions {
    fun selectDay(index: Int)
}

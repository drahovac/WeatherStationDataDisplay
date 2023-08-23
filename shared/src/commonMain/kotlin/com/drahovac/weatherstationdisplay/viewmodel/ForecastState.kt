package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.NetworkError
import dev.icerock.moko.resources.StringResource
import kotlinx.datetime.LocalDateTime

data class ForecastState(
    val error: NetworkError? = null,
    val days: List<ForecastDayState> = emptyList(),
    val selectedDayIndex: Int = 0,
) {
    val selectedDay = days.getOrNull(selectedDayIndex)
}

data class ForecastDayState(
    val dateTime: LocalDateTime,
    val temperatureMax: Int?,
    val temperatureMin: Int?,
    val snowOutlook: Double,
    val rainOutlook: Double,
    val sunrise: String,
    val sunset: String,
    val icon: Int,
    val narrative: String,
    val moonPhase: MoonPhase,
    val moonPhaseDesc: String,
    val dayParts: List<DayPartState>,
    val uvIndex: String,
)

data class DayPartState(
    val name: String,
    val icon: Int,
    val narrative: String,
    val precipChance: Int,
    val precipDesc: StringResource?,
    val relativeHumidity: Int,
)

enum class MoonPhase {
    NEW, FIRST_QUARTER, FULL, LAST_QUARTER,
    WANING_CRESCENT, WANING_GIBBOUS, WAXING_CRESCENT, WAXING_GIBBOUS,
}

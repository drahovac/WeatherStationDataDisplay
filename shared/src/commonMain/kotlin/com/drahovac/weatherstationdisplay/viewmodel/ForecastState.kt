package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.NetworkError
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
    val icon: Int,
    val narrative: String,
)

package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import kotlinx.serialization.Serializable

@Serializable
class HistoryWeatherDto(
    val observations: List<HistoryObservation>
)

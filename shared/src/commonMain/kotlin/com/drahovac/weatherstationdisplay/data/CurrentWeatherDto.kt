package com.drahovac.weatherstationdisplay.data

import com.drahovac.weatherstationdisplay.domain.CurrentWeatherObservation
import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherDto(
    val observations: List<CurrentWeatherObservation>
)

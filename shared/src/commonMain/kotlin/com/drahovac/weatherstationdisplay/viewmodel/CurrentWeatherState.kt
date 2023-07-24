package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.CurrentWeatherObservation
import com.drahovac.weatherstationdisplay.domain.NetworkError

data class CurrentWeatherState(
    val observation: CurrentWeatherObservation? = null,
    val error: NetworkError? = null
)

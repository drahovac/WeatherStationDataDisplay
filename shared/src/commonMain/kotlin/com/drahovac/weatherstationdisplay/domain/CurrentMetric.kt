package com.drahovac.weatherstationdisplay.domain

import kotlinx.serialization.Serializable

@Serializable
data class CurrentMetric(
    val temp: Double,
    val heatIndex: Double,
    val dewpt: Double,
    val windChill: Double,
    val windSpeed: Double,
    val windGust: Double,
    val pressure: Double,
    val precipRate: Double,
    val precipTotal: Double,
    val elev: Double
)
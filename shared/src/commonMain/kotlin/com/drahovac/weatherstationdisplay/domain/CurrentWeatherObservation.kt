package com.drahovac.weatherstationdisplay.domain

import kotlinx.serialization.Serializable

@Serializable
data class CurrentWeatherObservation(
    val stationID: String,
    val obsTimeUtc: String,
    val obsTimeLocal: String,
    val neighborhood: String?,
    val softwareType: String?,
    val country: String?,
    val solarRadiation: Double?,
    val lon: Double?,
    val realtimeFrequency: String?,
    val epoch: Long,
    val lat: Double?,
    val uv: Double?,
    val winddir: Int?,
    val humidity: Double?,
    val qcStatus: Int?,
    val metric: CurrentMetric
) {
    val stationCode: String = "${lat},${lon}"
}

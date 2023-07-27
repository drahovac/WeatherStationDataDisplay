package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class HistoryObservation(
    val stationID: String,
    val tz: String,
    val obsTimeUtc: Instant,
    val obsTimeLocal: String,
    val epoch: Long,
    val lat: Double,
    val lon: Double,
    val solarRadiationHigh: Double,
    val uvHigh: Double,
    val winddirAvg: Double,
    val humidityHigh: Double,
    val humidityLow: Double,
    val humidityAvg: Double,
    val qcStatus: Int,
    val metric: HistoryMetric
)

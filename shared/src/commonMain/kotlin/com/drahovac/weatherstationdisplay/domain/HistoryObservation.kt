package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable

@Serializable
data class HistoryObservation(
    val stationID: String,
    val tz: String,
    val obsTimeUtc: Instant,
    private val obsTimeLocal: String,
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
    val metric: HistoryMetric,
) {
    val dateTimeLocal: LocalDateTime
        // BE format with space, datetime x cannot parse...
        get() = LocalDateTime.parse(obsTimeLocal.replace(" ", "T").substring(0, 19))
}

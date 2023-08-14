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
    val isNoData: Boolean = false,
) {
    val dateTimeLocal: LocalDateTime
        // BE format with space, datetime x cannot parse...
        get() = LocalDateTime.parse(obsTimeLocal.replace(" ", "T").substring(0, 19))
}

fun HistoryObservation.Companion.getEmptyObservation(dateUTC: LocalDate): HistoryObservation {
    return HistoryObservation(
        "",
        "",
        dateUTC.atStartOfDayIn(TimeZone.UTC),
        dateUTC.atTime(23, 59, 0).toInstant(TimeZone.currentSystemDefault()).toString(),
        0L,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        1,
        HistoryMetric(
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
        ),
        isNoData = true
    )
}

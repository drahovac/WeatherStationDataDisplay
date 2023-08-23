package com.drahovac.weatherstationdisplay.domain

import kotlinx.serialization.Serializable

@Serializable
data class HistoryMetric(
    val tempHigh: Double?,
    val tempLow: Double?,
    val tempAvg: Double?,
    val windspeedHigh: Double?,
    val windspeedLow: Double?,
    val windspeedAvg: Double?,
    val windgustHigh: Double?,
    val windgustLow: Double?,
    val windgustAvg: Double?,
    val dewptHigh: Double?,
    val dewptLow: Double?,
    val dewptAvg: Double?,
    val windchillHigh: Double?,
    val windchillLow: Double?,
    val windchillAvg: Double?,
    val heatindexHigh: Double?,
    val heatindexLow: Double?,
    val heatindexAvg: Double?,
    val pressureMax: Double?,
    val pressureMin: Double?,
    val pressureTrend: Double?,
    val precipRate: Double?,
    val precipTotal: Double?,
)

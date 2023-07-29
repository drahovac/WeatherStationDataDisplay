package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

val historyObservationPrototype = HistoryObservation(
    stationID = "ABC123",
    tz = "UTC",
    obsTimeUtc = LocalDateTime.parse("2023-07-29T10:34:56").toInstant(TimeZone.UTC),
    obsTimeLocal = "2023-07-29T12:34:56",
    epoch = 1627568096L,
    lat = 37.7749,
    lon = -122.4194,
    solarRadiationHigh = 1450.2,
    uvHigh = 8.3,
    winddirAvg = 180.0,
    humidityHigh = 85.0,
    humidityLow = 50.0,
    humidityAvg = 68.5,
    qcStatus = 1,
    metric = historyMetricPrototype
)

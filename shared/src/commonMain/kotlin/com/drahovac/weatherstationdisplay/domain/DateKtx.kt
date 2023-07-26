package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

fun LocalDate.toCurrentUTCMillis() =
    this.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

fun LocalDate.Companion.fromUTCEpochMillis(millis: Long) =
    Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date

expect fun LocalDate.toFormattedDate(): String
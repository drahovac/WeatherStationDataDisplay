package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun LocalDate.toCurrentUTCMillis() =
    this.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()

fun LocalDate.toCurrentUTCMillisEndOFDay() =
    this.atTime(LocalTime(23, 59, 59)).toInstant(TimeZone.UTC).toEpochMilliseconds()

fun LocalDateTime.toCurrentUTCDays() =
    this.toInstant(TimeZone.UTC).toEpochMilliseconds() / 86400000


fun LocalDate.Companion.fromUTCEpochMillis(millis: Long) =
    Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date

fun Instant.toEpochDays() = toEpochMilliseconds() / 86400000

expect fun LocalDate.toFormattedDate(): String
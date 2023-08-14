package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toJavaLocalDate
import java.time.DayOfWeek
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

actual fun LocalDate.toFormattedDate(): String {
    return this.toJavaLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}

actual fun LocalDate.Companion.firstDayOfWeekIndex(): Int {
    return WeekFields.of(Locale.getDefault()).firstDayOfWeek.isoDayNumber
}

actual fun LocalDate.toLocalizedShortDayName(): String {
    return DayOfWeek.of(this.dayOfWeek.isoDayNumber)
        .getDisplayName(TextStyle.SHORT, Locale.getDefault())
}

actual fun LocalDate.toLocalizedLongDayName(): String {
    return DayOfWeek.of(this.dayOfWeek.isoDayNumber)
        .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
}

actual fun LocalDate.toLocalizedMontName(): String {
    return Month.of(this.monthNumber)
        .getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
}
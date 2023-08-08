package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.WeekFields
import java.util.Locale

actual fun LocalDate.toFormattedDate(): String {
    return this.toJavaLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}

actual fun LocalDate.Companion.firstDayOfWeekIndex(): Int {
    return WeekFields.of(Locale.getDefault()).firstDayOfWeek.isoDayNumber
}
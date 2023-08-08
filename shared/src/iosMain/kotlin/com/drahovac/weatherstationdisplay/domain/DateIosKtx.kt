package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSCalendar
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.autoupdatingCurrentLocale
import platform.Foundation.localTimeZone

actual fun LocalDate.toFormattedDate(): String {
    val dateFormatter = NSDateFormatter().apply {
        timeZone = NSTimeZone.localTimeZone
        locale = NSLocale.autoupdatingCurrentLocale
        dateStyle = NSDateFormatterMediumStyle
    }

    return this.toNSDateComponents().date?.let { dateFormatter.stringFromDate(it) } ?: ""
}

actual fun LocalDate.Companion.firstDayOfWeekIndex(): Int {
    return NSCalendar.currentCalendar.firstWeekday().toInt()
}

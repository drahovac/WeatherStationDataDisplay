package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Test
import java.util.Locale
import kotlin.test.AfterTest
import kotlin.test.assertEquals

class DateAndroidKtxTest {

    private val systemLocale = Locale.getDefault()
    private val clock = object : Clock {
        override fun now(): Instant {
            return TODAY.toInstant(TimeZone.UTC)
        }
    }

    @AfterTest
    fun tearDown() {
        Locale.setDefault(systemLocale)
    }

    @Test
    fun `return first cs day of week`() {
        Locale.setDefault(Locale("cs", "CZ"))

        val day = clock.firstDayOfWeek()

        assertEquals(DayOfWeek.MONDAY, day.dayOfWeek)
        assertEquals("31. 7. 2023", day.toFormattedDate())
    }

    @Test
    fun `return first en day of week`() {
        Locale.setDefault(Locale.ENGLISH)

        val day = clock.firstDayOfWeek()

        assertEquals(DayOfWeek.SUNDAY, day.dayOfWeek)
        assertEquals("Jul 30, 2023", day.toFormattedDate())
    }

    @Test
    fun `format cs locale`() {
        Locale.setDefault(Locale("cs", "CZ"))

        assertEquals("24. 5. 2023", DATE_1.toFormattedDate())
        assertEquals("1. 1. 2023", DATE_2.toFormattedDate())
        assertEquals("20. 10. 2023", DATE_3.toFormattedDate())
    }

    @Test
    fun `format en locale`() {
        Locale.setDefault(Locale.ENGLISH)

        assertEquals("May 24, 2023", DATE_1.toFormattedDate())
        assertEquals("Jan 1, 2023", DATE_2.toFormattedDate())
        assertEquals("Oct 20, 2023", DATE_3.toFormattedDate())
    }

    private companion object {
        val TODAY = LocalDateTime.parse("2023-07-31T03:06")
        val DATE_1 = LocalDate.parse("2023-05-24")
        val DATE_2 = LocalDate.parse("2023-01-01")
        val DATE_3 = LocalDate.parse("2023-10-20")
    }
}
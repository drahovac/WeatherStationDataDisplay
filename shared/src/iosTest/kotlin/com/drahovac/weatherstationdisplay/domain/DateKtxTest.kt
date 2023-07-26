package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class DateKtxTest {

    @Test
    fun `format en locale`() {
        assertEquals("May 24, 2023", DATE_1.toFormattedDate())
        assertEquals("Jan 1, 2023", DATE_2.toFormattedDate())
        assertEquals("Oct 20, 2023", DATE_3.toFormattedDate())
    }

    private companion object {
        val DATE_1 = LocalDate.parse("2023-05-24")
        val DATE_2 = LocalDate.parse("2023-01-01")
        val DATE_3 = LocalDate.parse("2023-10-20")
    }
}

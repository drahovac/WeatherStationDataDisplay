package com.drahovac.weatherstationdisplay.data

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import org.junit.Test

class HistoryWeatherDtoTest {

    @Test
    fun `parse date time`() {
        Instant.parse("2023-07-19T21:59:53Z")
    }
}
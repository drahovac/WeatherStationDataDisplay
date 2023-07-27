package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.LocalDate

interface HistoryWeatherDataRepository {
    suspend fun fetchHistory(startDate: LocalDate): Result<List<HistoryObservation>>
}

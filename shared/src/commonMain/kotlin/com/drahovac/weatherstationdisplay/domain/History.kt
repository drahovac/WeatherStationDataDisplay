package com.drahovac.weatherstationdisplay.domain

import kotlinx.datetime.LocalDate

data class History(
    val firstDate: LocalDate,
    val lastDate: LocalDate,
    val observations: List<HistoryObservation>,
)
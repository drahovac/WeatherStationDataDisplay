package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.HistoryObservation

data class HistoryDataState(
    val isLoading: Boolean = false,
    val history: List<HistoryObservation> = emptyList()
)

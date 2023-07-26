package com.drahovac.weatherstationdisplay.viewmodel

import kotlinx.datetime.LocalDate

data class HistoryState(
    val noData: HistoryNoData? = null
)

data class HistoryNoData(
    val isPickerVisible: Boolean = false,
    val startDate: LocalDate? = null
)

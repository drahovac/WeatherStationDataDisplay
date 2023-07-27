package com.drahovac.weatherstationdisplay.viewmodel

import dev.icerock.moko.resources.StringResource
import kotlinx.datetime.LocalDate

data class HistoryState(
    val noData: HistoryNoData? = null,
    val isLoading: Boolean = false,
)

data class HistoryNoData(
    val isPickerVisible: Boolean = false,
    val startDate: LocalDate? = null,
    val error: StringResource? = null,
)

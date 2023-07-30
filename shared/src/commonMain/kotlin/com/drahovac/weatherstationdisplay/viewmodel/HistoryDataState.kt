package com.drahovac.weatherstationdisplay.viewmodel

import kotlinx.datetime.LocalDate

data class HistoryDataState(
    val isLoading: Boolean = false,
    val selectedTab: HistoryDataTab = HistoryDataTab.WEEK,
    val tabData: Map<HistoryDataTab, HistoryTabData?> = mapOf()
) {
    val currentTabData: HistoryTabData?
        get() = tabData[selectedTab]
}

enum class HistoryDataTab {
    YESTERDAY, WEEK, MONTH
}

data class HistoryTabData(
    val maxTemperature: Double,
    val maxDate: LocalDate,
    val minTemperature: Double,
    val minDate: LocalDate,
    val tempChartModel: ChartModel
)

expect class ChartModel

expect fun List<List<Pair<LocalDate, Double>>>.toChartModel(): ChartModel

package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import kotlinx.datetime.LocalDate

data class HistoryDataState(
    val isLoading: Boolean = false,
    val selectedTab: HistoryDataTab = HistoryDataTab.WEEK,
    val tabData: Map<HistoryDataTab, HistoryTabData?> = mapOf(),
) {
    val currentTabData: HistoryTabData?
        get() = tabData[selectedTab]
}

data class TempChartSets(
    val isMaxAllowed: Boolean = true,
    val isMinAllowed: Boolean = true,
    val isAvgAllowed: Boolean = true,
)

enum class HistoryDataTab {
    YESTERDAY, WEEK, MONTH
}

data class HistoryTabData(
    val maxTemperature: Double,
    val maxDate: LocalDate,
    val minTemperature: Double,
    val minDate: LocalDate,
    val observations: List<HistoryObservation>,
    val tempChartSets: TempChartSets = TempChartSets(),
    val tempChartModel: ChartModel
)

expect class ChartModel

expect fun List<List<Pair<LocalDate, Double>>>.toChartModel(): ChartModel

package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import kotlinx.datetime.LocalDate

data class HistoryDataState(
    val isLoading: Boolean = false,
    val selectedTab: HistoryTab = HistoryTab.WEEK,
    val tabData: Map<HistoryTab, HistoryTabState?> = mapOf(),
) {
    val currentTabData: HistoryTabState?
        get() = tabData[selectedTab]
}

data class TempChartSets(
    val isMaxAllowed: Boolean = true,
    val isMinAllowed: Boolean = true,
    val isAvgAllowed: Boolean = true,
)

enum class HistoryTab {
    YESTERDAY, WEEK, MONTH
}

data class HistoryTabState(
    val maxTemperature: Double,
    val maxDate: LocalDate,
    val minTemperature: Double,
    val minDate: LocalDate,
    val observations: List<HistoryObservation>,
    val tempChartSets: TempChartSets = TempChartSets(),
    val tempChartModel: ChartModel
)

expect class ChartModel

expect fun List<List<Pair<LocalDate, Double>>>.toChartModel(defaultDaysCount: Float): ChartModel

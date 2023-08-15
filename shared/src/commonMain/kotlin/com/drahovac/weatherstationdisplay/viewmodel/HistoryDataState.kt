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
) {
    fun isNotEmpty() = isMaxAllowed || isMinAllowed || isAvgAllowed
}

enum class HistoryTab {
    YESTERDAY, WEEK, MONTH
}

data class HistoryTabState(
    val startDate: LocalDate,
    val temperature: TemperatureState,
    val uv: UvState,
)

data class TemperatureState(
    val maxTemperature: Double,
    val maxDate: LocalDate,
    val minTemperature: Double,
    val minDate: LocalDate,
    val tempChart: ChartState<TempChartSelection>,
)

data class UvState(
    val maxUvIndex: Int,
    val maxRadiation: Double,
    val maxUvDate: LocalDate,
    val maxRadiationDate: LocalDate,
)

data class ChartState<T : ChartSelection>(
    val observations: List<HistoryObservation>,
    val tempChartSets: TempChartSets = TempChartSets(),
    val selectedEntries: T? = null,
    val bottomLabels: List<String> = emptyList(),
    val tempChartModel: ChartModel
) {
    val hasMultipleItems: Boolean
        get() = observations.size > 1 // cannot show chart with single value (lib is bad)
}

interface ChartSelection

data class TempChartSelection(
    val maxTemp: ChartPointEntry?,
    val avgTemp: ChartPointEntry?,
    val minTemp: ChartPointEntry?,
    val date: LocalDate
) : ChartSelection

expect class ChartModel

expect interface ChartPointEntry

expect fun List<List<Pair<LocalDate, Double>>>.toChartModel(defaultDaysCount: Float): ChartModel

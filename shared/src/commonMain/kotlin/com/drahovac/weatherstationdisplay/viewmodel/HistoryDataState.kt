package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.History
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

data class PressureSets(
    val isMaxAllowed: Boolean = true,
    val isMinAllowed: Boolean = true,
) {
    fun isNotEmpty() = isMaxAllowed || isMinAllowed
}

enum class HistoryTab {
    YESTERDAY, WEEK, MONTH
}

data class HistoryTabState(
    val startDate: LocalDate,
    val temperature: TemperatureState,
    val uv: UvState,
    val pressure: PressureState,
    val prescriptionForPeriod: Double,
    val maxWindSpeed: Double,
    val maxWindSpeedDate: LocalDate,
) {
    val hasData: Boolean = temperature.history.observations.isNotEmpty()
}

data class TemperatureState(
    val maxTemperature: Double,
    val maxDate: LocalDate,
    val minTemperature: Double,
    val minDate: LocalDate,
    val chart: ChartState<TempChartSelection, TempChartSets>,
) {
    val history: History
        get() = History(chart.startDate, chart.endDate, chart.observations)
}

data class UvState(
    val maxUvIndex: Int,
    val maxRadiation: Double,
    val maxUvDate: LocalDate,
    val maxRadiationDate: LocalDate,
)

data class PressureState(
    val maxPressure: Double,
    val maxPressureDate: LocalDate,
    val minPressure: Double,
    val minPressureDate: LocalDate,
    val trends: List<Double>,
    val chart: ChartState<PressureChartSelection, PressureSets>,
)

data class ChartState<T : ChartSelection, S>(
    val observations: List<HistoryObservation>,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val chartSets: S,
    val selectedEntries: T? = null,
    val bottomLabels: List<String> = emptyList(),
    val chartModel: ChartModel
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

data class PressureChartSelection(
    val maxPressure: ChartPointEntry?,
    val minPressure: ChartPointEntry?,
    val trend: Double?,
    val date: LocalDate
) : ChartSelection

expect class ChartModel

expect interface ChartPointEntry

expect fun List<List<Pair<LocalDate, Double>>>.toChartModel(
    defaultDaysCount: Float, minY: Float,
    maxY: Float,
    xOffset: Float,
): ChartModel

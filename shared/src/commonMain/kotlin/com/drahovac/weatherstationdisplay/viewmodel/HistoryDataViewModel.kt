package com.drahovac.weatherstationdisplay.viewmodel

import co.touchlab.kermit.Logger
import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.domain.toFormattedDate
import com.drahovac.weatherstationdisplay.domain.toLocalizedShortDayName
import com.drahovac.weatherstationdisplay.usecase.HistoryUseCase
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.coroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

class HistoryDataViewModel(
    private val historyUseCase: HistoryUseCase,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : KMMViewModel(), HistoryDataActions {

    private val _state = MutableStateFlow(
        HistoryDataState()
    )

    @NativeCoroutines
    val state = _state

    init {
        viewModelScope.coroutineScope.launch {
            _state.update { it.copy(isLoading = true) }
            historyUseCase.fetchHistoryUpToDate()
            _state.update {
                it.copy(
                    isLoading = false,
                    selectedTab = HistoryTab.WEEK,
                    tabData = mapOf(
                        HistoryTab.WEEK to fetchTabData(
                            HistoryTab.WEEK,
                            TempChartSets(),
                            PressureSets()
                        )
                    )
                )
            }
        }
    }

    override fun selectTab(tab: HistoryTab) {
        _state.update {
            it.copy(selectedTab = tab)
        }
        viewModelScope.coroutineScope.launch {
            _state.update { currentState ->
                val tabData =
                    currentState.tabData[tab] ?: fetchTabData(tab, TempChartSets(), PressureSets())
                val tabMap = currentState.tabData.toMutableMap().apply {
                    put(tab, tabData)
                }
                currentState.copy(
                    isLoading = false,
                    selectedTab = tab,
                    tabData = tabMap,
                )
            }
        }
    }

    override fun selectMaxTempChart(isSelected: Boolean) {
        _state.update {
            val tempChartSets =
                it.currentTabData?.temperature?.chart?.chartSets?.copy(isMaxAllowed = isSelected)
            val tabData = getTabData(
                it,
                tempChartSets ?: TempChartSets(isMaxAllowed = isSelected),
                defaultPressureSets(it)
            )
            it.copy(tabData = tabData)
        }
    }

    private fun defaultPressureSets(state: HistoryDataState) =
        state.currentTabData?.pressure?.chart?.chartSets ?: PressureSets()

    private fun defaultTempSets(state: HistoryDataState) =
        state.currentTabData?.temperature?.chart?.chartSets ?: TempChartSets()

    private fun getTabData(
        state: HistoryDataState,
        tempChartSets: TempChartSets,
        pressureChartSets: PressureSets,
    ): MutableMap<HistoryTab, HistoryTabState?> {
        return getTabData(state) {
            it.temperature.chart.observations.toTabData(
                tempChartSets,
                pressureChartSets,
                state.selectedTab
            ) ?: it
        }
    }

    private fun getTabData(
        state: HistoryDataState,
        updateTab: (HistoryTabState) -> HistoryTabState,
    ): MutableMap<HistoryTab, HistoryTabState?> {
        return state.tabData.toMutableMap().apply {
            val removed = remove(state.selectedTab)
            put(
                state.selectedTab,
                removed?.let { updateTab(it) }
            )
        }
    }

    override fun selectMinTempChart(isSelected: Boolean) {
        _state.update {
            val tempChartSets =
                it.currentTabData?.temperature?.chart?.chartSets?.copy(isMinAllowed = isSelected)
            val tabData = getTabData(
                it,
                tempChartSets ?: TempChartSets(isMinAllowed = isSelected),
                defaultPressureSets(it)
            )
            it.copy(tabData = tabData)
        }
    }

    override fun selectMaxPressureChart(isSelected: Boolean) {
        _state.update {
            val pressureSets =
                it.currentTabData?.pressure?.chart?.chartSets?.copy(isMaxAllowed = isSelected)
            val tabData = getTabData(
                it,
                defaultTempSets(it),
                pressureSets ?: PressureSets(isMaxAllowed = isSelected)
            )
            it.copy(tabData = tabData)
        }
    }

    override fun selectMinPressureChart(isSelected: Boolean) {
        _state.update {
            val pressureSets =
                it.currentTabData?.pressure?.chart?.chartSets?.copy(isMinAllowed = isSelected)
            val tabData = getTabData(
                it,
                defaultTempSets(it),
                pressureSets ?: PressureSets(isMinAllowed = isSelected)
            )
            it.copy(tabData = tabData)
        }
    }

    override fun selectAvgTempChart(isSelected: Boolean) {
        _state.update {
            val tempChartSets =
                it.currentTabData?.temperature?.chart?.chartSets?.copy(isAvgAllowed = isSelected)
            val tabData = getTabData(
                it,
                tempChartSets ?: TempChartSets(isAvgAllowed = isSelected),
                defaultPressureSets(it)
            )
            it.copy(tabData = tabData)
        }
    }

    override fun selectTempPoints(points: List<ChartPointEntry>, dayIndex: Int) {
        _state.update {
            it.copy(tabData = getTabData(it) { tabState ->
                val tempChart = tabState.temperature.chart.copy(
                    selectedEntries = getSelectedTempEntries(
                        points,
                        tabState.temperature.chart.chartSets,
                        tabState.startDate.plus(dayIndex, DateTimeUnit.DAY)
                    )
                )
                tabState.copy(
                    temperature = tabState.temperature.copy(
                        chart = tempChart
                    )
                )
            })
        }
    }

    override fun selectPressurePoints(points: List<ChartPointEntry>, dayIndex: Int) {
        _state.update {
            it.copy(tabData = getTabData(it) { tabState ->
                val pressureChart = tabState.pressure.chart.copy(
                    selectedEntries = getSelectedPressureEntries(
                        points,
                        tabState.pressure.chart.chartSets,
                        tabState.startDate.plus(dayIndex, DateTimeUnit.DAY),
                        tabState.pressure.trends.getOrNull(dayIndex),
                    )
                )
                tabState.copy(
                    pressure = tabState.pressure.copy(
                        chart = pressureChart
                    ),
                )
            })
        }
    }

    private fun getSelectedTempEntries(
        points: List<ChartPointEntry>,
        tempChartSets: TempChartSets,
        date: LocalDate,
    ): TempChartSelection? {
        return if (points.isNotEmpty() && tempChartSets.isNotEmpty()) {
            var index = 0
            val increaseIndex = { index += 1 }

            TempChartSelection(
                maxTemp = if (tempChartSets.isMaxAllowed) points.getOrNull(index)
                    .also { increaseIndex() } else null,
                avgTemp = if (tempChartSets.isAvgAllowed) points.getOrNull(index)
                    .also { increaseIndex() } else null,
                minTemp = if (tempChartSets.isMinAllowed) points.getOrNull(index)
                    .also { increaseIndex() } else null,
                date = date,
            )
        } else null
    }

    private fun getSelectedPressureEntries(
        points: List<ChartPointEntry>,
        pressureSets: PressureSets,
        date: LocalDate,
        trend: Double?,
    ): PressureChartSelection? {
        return if (points.isNotEmpty() && pressureSets.isNotEmpty()) {
            var index = 0
            val increaseIndex = { index += 1 }

            PressureChartSelection(
                maxPressure = if (pressureSets.isMaxAllowed) points.getOrNull(index)
                    .also { increaseIndex() } else null,
                minPressure = if (pressureSets.isMinAllowed) points.getOrNull(index)
                    .also { increaseIndex() } else null,
                trend = trend.takeIf { pressureSets.isMinAllowed && pressureSets.isMaxAllowed },
                date = date,
            )
        } else null
    }

    private suspend fun fetchTabData(
        tab: HistoryTab,
        tempChartSets: TempChartSets,
        pressureChartSets: PressureSets,
    ): HistoryTabState? {
        return withContext(defaultDispatcher) {
            when (tab) {
                HistoryTab.YESTERDAY -> historyUseCase.getYesterdayHistory()
                HistoryTab.WEEK -> historyUseCase.getWeekHistory()
                HistoryTab.MONTH -> historyUseCase.getMonthHistory()
            }
        }.toTabData(tempChartSets, pressureChartSets, tab)
    }
}

fun List<HistoryObservation>.toTabData(
    tempChartSets: TempChartSets,
    pressureSets: PressureSets,
    tab: HistoryTab,
): HistoryTabState? {
    Logger.d("Loaded observations $this")
    if (isEmpty()) return null
    val minTemperature = minBy { it.metric.tempLow }
    val maxTemperature = maxBy { it.metric.tempHigh }
    val maxUV = maxBy { it.uvHigh }
    val maxRadiation = maxBy { it.solarRadiationHigh }
    val maxTemperatures =
        map { it.dateTimeLocal.date to it.metric.tempHigh }.sortedBy { it.first.toEpochDays() }
    val avgTemperatures =
        map { it.dateTimeLocal.date to it.metric.tempAvg }.sortedBy { it.first.toEpochDays() }
    val minTemperatures =
        map { it.dateTimeLocal.date to it.metric.tempLow }.sortedBy { it.first.toEpochDays() }
    val prescriptionForPeriod = sumOf { it.metric.precipTotal }
    val windSpeedMax = maxBy { it.metric.windspeedHigh }
    val tempChartModel = listOfNotNull(
        maxTemperatures.takeIf { tempChartSets.isMaxAllowed },
        avgTemperatures.takeIf { tempChartSets.isAvgAllowed },
        minTemperatures.takeIf { tempChartSets.isMinAllowed },
    )
    val maxPressure = maxBy { it.metric.pressureMax }
    val maxPressures =
        map { it.dateTimeLocal.date to it.metric.pressureMax }.sortedBy { it.first.toEpochDays() }
    val minPressure = minBy { it.metric.pressureMin }
    val minPressures =
        map { it.dateTimeLocal.date to it.metric.pressureMin }.sortedBy { it.first.toEpochDays() }
    val pressureModel = listOfNotNull(
        maxPressures.takeIf { pressureSets.isMaxAllowed },
        minPressures.takeIf { pressureSets.isMinAllowed },
    )
    return HistoryTabState(
        startDate = maxTemperatures.firstOrNull()?.first ?: Clock.System.now().toLocalDateTime(
            TimeZone.UTC
        ).date,
        uv = UvState(
            maxUvIndex = maxUV.uvHigh.toInt(),
            maxRadiation = maxRadiation.solarRadiationHigh,
            maxUvDate = maxUV.dateTimeLocal.date,
            maxRadiationDate = maxRadiation.dateTimeLocal.date,
        ),
        temperature = TemperatureState(
            maxTemperature = maxTemperature.metric.tempHigh,
            minTemperature = minTemperature.metric.tempLow,
            maxDate = maxTemperature.dateTimeLocal.date,
            minDate = minTemperature.dateTimeLocal.date,
            chart = ChartState(
                observations = this,
                chartSets = tempChartSets,
                selectedEntries = null,
                bottomLabels = bottomLabels(tab),
                chartModel = tempChartModel.toChartModel(tab.daysCount, 0f, 25f)
            )
        ),
        pressure = PressureState(
            maxPressure = maxPressure.metric.pressureMax,
            maxPressureDate = maxPressure.dateTimeLocal.date,
            minPressure = minPressure.metric.pressureMin,
            minPressureDate = maxPressure.dateTimeLocal.date,
            trends = map { it.metric.pressureTrend },
            chart = ChartState(
                observations = this,
                chartSets = pressureSets,
                selectedEntries = null,
                bottomLabels = bottomLabels(tab),
                chartModel = pressureModel.toChartModel(tab.daysCount, 1000f, 1025f)
            ),
        ),
        maxWindSpeed = windSpeedMax.metric.windspeedHigh,
        maxWindSpeedDate = windSpeedMax.dateTimeLocal.date,
        prescriptionForPeriod = prescriptionForPeriod,
    )
}

private fun List<HistoryObservation>.bottomLabels(tab: HistoryTab): List<String> {
    val minDate = minBy { it.obsTimeUtc }.dateTimeLocal.date
    val maxDate = minDate.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY)
    return when (tab) {
        HistoryTab.MONTH -> listOf(
            minDate.toFormattedDate(),
            maxDate.toFormattedDate()
        )

        HistoryTab.YESTERDAY -> emptyList()
        HistoryTab.WEEK -> return List(7) { it }.map {
            minDate.plus(it, DateTimeUnit.DAY).toLocalizedShortDayName()
        }
    }
}

private val HistoryTab.daysCount: Float
    get() = when (this) {
        HistoryTab.YESTERDAY -> 1f
        HistoryTab.WEEK -> 6f
        HistoryTab.MONTH -> 30f
    }

interface HistoryDataActions {
    fun selectTab(tab: HistoryTab)

    fun selectMaxTempChart(isSelected: Boolean)

    fun selectMinTempChart(isSelected: Boolean)

    fun selectMaxPressureChart(isSelected: Boolean)

    fun selectMinPressureChart(isSelected: Boolean)

    fun selectAvgTempChart(isSelected: Boolean)

    fun selectTempPoints(points: List<ChartPointEntry>, dayIndex: Int)

    fun selectPressurePoints(points: List<ChartPointEntry>, dayIndex: Int)
}

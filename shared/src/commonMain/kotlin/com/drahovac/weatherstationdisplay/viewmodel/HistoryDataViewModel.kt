package com.drahovac.weatherstationdisplay.viewmodel

import co.touchlab.kermit.Logger
import com.drahovac.weatherstationdisplay.domain.History
import com.drahovac.weatherstationdisplay.domain.firstDayOfMonth
import com.drahovac.weatherstationdisplay.domain.firstDayOfWeek
import com.drahovac.weatherstationdisplay.domain.toEpochDays
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
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.until

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

    override fun selectNextMonth() {
        _state.value.currentTabData?.startDate?.let {
            updateSelectedTerm(it.firstDayOfMonth().plus(1, DateTimeUnit.MONTH), HistoryTab.MONTH)
        }
    }

    private fun updateSelectedTerm(newStartDate: LocalDate, tab: HistoryTab) {
        viewModelScope.coroutineScope.launch {
            _state.update { currentState ->
                val newData = fetchTabData(
                    tab,
                    currentState.currentTabData?.temperature?.chart?.chartSets ?: TempChartSets(),
                    currentState.currentTabData?.pressure?.chart?.chartSets ?: PressureSets(),
                    newStartDate
                )
                currentState.copy(
                    isLoading = false,
                    tabData = getTabData(currentState) { newData },
                )
            }
        }
    }

    override fun selectPreviousMonth() {
        _state.value.currentTabData?.startDate?.let {
            updateSelectedTerm(it.firstDayOfMonth().minus(1, DateTimeUnit.MONTH), HistoryTab.MONTH)
        }
    }

    override fun selectNextWeek() {
        _state.value.currentTabData?.startDate?.let {
            updateSelectedTerm(it.firstDayOfWeek().plus(1, DateTimeUnit.WEEK), HistoryTab.WEEK)
        }
    }

    override fun selectPreviousWeek() {
        _state.value.currentTabData?.startDate?.let {
            updateSelectedTerm(it.firstDayOfWeek().minus(1, DateTimeUnit.WEEK), HistoryTab.WEEK)
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
            it.temperature.history.toTabData(
                tempChartSets,
                pressureChartSets,
                state.selectedTab
            )
        }
    }

    private fun getTabData(
        state: HistoryDataState,
        updateTab: (HistoryTabState) -> HistoryTabState?,
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
        startDate: LocalDate? = null,
    ): HistoryTabState {
        return withContext(defaultDispatcher) {
            when (tab) {
                HistoryTab.YESTERDAY -> historyUseCase.getYesterdayHistory()
                HistoryTab.WEEK -> historyUseCase.getWeekHistory(startDate)
                HistoryTab.MONTH -> historyUseCase.getMonthHistory(startDate)
            }
        }.toTabData(tempChartSets, pressureChartSets, tab)
    }
}

fun History.toTabData(
    tempChartSets: TempChartSets,
    pressureSets: PressureSets,
    tab: HistoryTab,
): HistoryTabState {
    Logger.d("Loaded observations $this")
    with(this.observations) {
        val minTemperature = minByOrNull { it.metric.tempLow }
        val maxTemperature = maxByOrNull { it.metric.tempHigh }
        val maxUV = maxByOrNull { it.uvHigh }
        val maxRadiation = maxByOrNull { it.solarRadiationHigh }
        val maxTemperatures =
            map { it.dateTimeLocal.date to it.metric.tempHigh }.sortedBy { it.first.toEpochDays() }
        val avgTemperatures =
            map { it.dateTimeLocal.date to it.metric.tempAvg }.sortedBy { it.first.toEpochDays() }
        val minTemperatures =
            map { it.dateTimeLocal.date to it.metric.tempLow }.sortedBy { it.first.toEpochDays() }
        val prescriptionForPeriod = sumOf { it.metric.precipTotal }
        val windSpeedMax = maxByOrNull { it.metric.windspeedHigh }
        val tempChartModel = listOfNotNull(
            maxTemperatures.takeIf { tempChartSets.isMaxAllowed },
            avgTemperatures.takeIf { tempChartSets.isAvgAllowed },
            minTemperatures.takeIf { tempChartSets.isMinAllowed },
        )
        val maxPressure = maxByOrNull { it.metric.pressureMax }
        val maxPressures =
            map { it.dateTimeLocal.date to it.metric.pressureMax }.sortedBy { it.first.toEpochDays() }
        val minPressure = minByOrNull { it.metric.pressureMin }
        val minPressures =
            map { it.dateTimeLocal.date to it.metric.pressureMin }.sortedBy { it.first.toEpochDays() }
        val pressureModel = listOfNotNull(
            maxPressures.takeIf { pressureSets.isMaxAllowed },
            minPressures.takeIf { pressureSets.isMinAllowed },
        )
        val startDate =
            this.minByOrNull { it.obsTimeUtc.toEpochDays() }?.obsTimeUtc?.toLocalDateTime(
                TimeZone.UTC
            )?.date ?: firstDate
        val xOffset =
            if (startDate <= firstDate) 0f else firstDate.until(startDate, DateTimeUnit.DAY)
                .toFloat()
        return HistoryTabState(
            startDate = startDate,
            uv = UvState(
                maxUvIndex = maxUV?.uvHigh?.toInt() ?: 0,
                maxRadiation = maxRadiation?.solarRadiationHigh ?: 0.0,
                maxUvDate = maxUV?.dateTimeLocal?.date ?: startDate,
                maxRadiationDate = maxRadiation?.dateTimeLocal?.date ?: startDate,
            ),
            temperature = TemperatureState(
                maxTemperature = maxTemperature?.metric?.tempHigh ?: 0.0,
                minTemperature = minTemperature?.metric?.tempLow ?: 0.0,
                maxDate = maxTemperature?.dateTimeLocal?.date ?: startDate,
                minDate = minTemperature?.dateTimeLocal?.date ?: startDate,
                chart = ChartState(
                    observations = this,
                    chartSets = tempChartSets,
                    selectedEntries = null,
                    bottomLabels = bottomLabels(tab),
                    startDate = this@toTabData.firstDate,
                    endDate = this@toTabData.lastDate,
                    chartModel = tempChartModel.toChartModel(tab.daysCount, 0f, 25f, xOffset)
                )
            ),
            pressure = PressureState(
                maxPressure = maxPressure?.metric?.pressureMax ?: 0.0,
                maxPressureDate = maxPressure?.dateTimeLocal?.date ?: startDate,
                minPressure = minPressure?.metric?.pressureMin ?: 0.0,
                minPressureDate = maxPressure?.dateTimeLocal?.date ?: startDate,
                trends = map { it.metric.pressureTrend },
                chart = ChartState(
                    observations = this,
                    chartSets = pressureSets,
                    selectedEntries = null,
                    bottomLabels = bottomLabels(tab),
                    startDate = this@toTabData.firstDate,
                    endDate = this@toTabData.lastDate,
                    chartModel = pressureModel.toChartModel(tab.daysCount, 1000f, 1025f, xOffset)
                ),
            ),
            maxWindSpeed = windSpeedMax?.metric?.windspeedHigh ?: 0.0,
            maxWindSpeedDate = windSpeedMax?.dateTimeLocal?.date ?: startDate,
            prescriptionForPeriod = prescriptionForPeriod,
        )
    }
}

private fun History.bottomLabels(tab: HistoryTab): List<String> {
    return when (tab) {
        HistoryTab.MONTH -> listOf(
            firstDate.toFormattedDate(),
            lastDate.toFormattedDate()
        )

        HistoryTab.YESTERDAY -> emptyList()
        HistoryTab.WEEK -> return List(7) { it }.map {
            firstDate.plus(it, DateTimeUnit.DAY).toLocalizedShortDayName()
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

    fun selectNextMonth()

    fun selectPreviousMonth()

    fun selectNextWeek()

    fun selectPreviousWeek()

    fun selectMaxTempChart(isSelected: Boolean)

    fun selectMinTempChart(isSelected: Boolean)

    fun selectMaxPressureChart(isSelected: Boolean)

    fun selectMinPressureChart(isSelected: Boolean)

    fun selectAvgTempChart(isSelected: Boolean)

    fun selectTempPoints(points: List<ChartPointEntry>, dayIndex: Int)

    fun selectPressurePoints(points: List<ChartPointEntry>, dayIndex: Int)
}

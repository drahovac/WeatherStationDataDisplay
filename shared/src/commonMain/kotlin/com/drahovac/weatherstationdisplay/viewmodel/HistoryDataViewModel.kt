package com.drahovac.weatherstationdisplay.viewmodel

import co.touchlab.kermit.Logger
import com.drahovac.weatherstationdisplay.domain.History
import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.domain.firstDayOfMonth
import com.drahovac.weatherstationdisplay.domain.firstDayOfWeek
import com.drahovac.weatherstationdisplay.domain.orZero
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
                            PressureSets(),
                            HumidityChartSets(),
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
                    currentState.tabData[tab] ?: fetchTabData(
                        tab,
                        TempChartSets(),
                        PressureSets(),
                        HumidityChartSets()
                    )
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
                    defaultTempSets(currentState),
                    defaultPressureSets(currentState),
                    defaultHumiditySets(currentState),
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
                defaultPressureSets(it),
                defaultHumiditySets(it)
            )
            it.copy(tabData = tabData)
        }
    }

    private fun defaultPressureSets(state: HistoryDataState) =
        state.currentTabData?.pressure?.chart?.chartSets ?: PressureSets()

    private fun defaultTempSets(state: HistoryDataState) =
        state.currentTabData?.temperature?.chart?.chartSets ?: TempChartSets()

    private fun defaultHumiditySets(state: HistoryDataState) =
        state.currentTabData?.humidity?.chartSets ?: HumidityChartSets()

    private fun getTabData(
        state: HistoryDataState,
        tempChartSets: TempChartSets,
        pressureChartSets: PressureSets,
        humidityChartSets: HumidityChartSets,
    ): MutableMap<HistoryTab, HistoryTabState?> {
        return getTabData(state) {
            it.temperature.history.toTabData(
                tempChartSets,
                pressureChartSets,
                humidityChartSets,
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
                defaultPressureSets(it),
                defaultHumiditySets(it),
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
                pressureSets ?: PressureSets(isMaxAllowed = isSelected),
                defaultHumiditySets(it)
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
                pressureSets ?: PressureSets(isMinAllowed = isSelected),
                defaultHumiditySets(it)
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
                defaultPressureSets(it),
                defaultHumiditySets(it),
            )
            it.copy(tabData = tabData)
        }
    }

    override fun selectMaxHumidity(isSelected: Boolean) {
        _state.update {
            val sets =
                it.currentTabData?.humidity?.chartSets?.copy(isMaxAllowed = isSelected)
            val tabData = getTabData(
                it,
                defaultTempSets(it),
                defaultPressureSets(it),
                sets ?: HumidityChartSets(isMaxAllowed = isSelected),
            )
            it.copy(tabData = tabData)
        }
    }

    override fun selectMinHumidity(isSelected: Boolean) {
        _state.update {
            val sets =
                it.currentTabData?.humidity?.chartSets?.copy(isMinAllowed = isSelected)
            val tabData = getTabData(
                it,
                defaultTempSets(it),
                defaultPressureSets(it),
                sets ?: HumidityChartSets(isMinAllowed = isSelected),
            )
            it.copy(tabData = tabData)
        }
    }

    override fun selectAvgHumidity(isSelected: Boolean) {
        _state.update {
            val sets =
                it.currentTabData?.humidity?.chartSets?.copy(isAvgAllowed = isSelected)
            val tabData = getTabData(
                it,
                defaultTempSets(it),
                defaultPressureSets(it),
                sets ?: HumidityChartSets(isAvgAllowed = isSelected),
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

    override fun selectHumidityPoints(points: List<ChartPointEntry>, dayIndex: Int) {
        _state.update {
            it.copy(tabData = getTabData(it) { tabState ->
                val humidityChart = tabState.humidity.copy(
                    selectedEntries = getSelectedHumidityEntries(
                        points,
                        tabState.humidity.chartSets,
                        tabState.startDate.plus(dayIndex, DateTimeUnit.DAY),
                    )
                )
                tabState.copy(
                    humidity = humidityChart,
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

    private fun getSelectedHumidityEntries(
        points: List<ChartPointEntry>,
        sets: HumidityChartSets,
        date: LocalDate,
    ): HumidityChartSelection? {
        return if (points.isNotEmpty() && sets.isNotEmpty()) {
            var index = 0
            val increaseIndex = { index += 1 }

            HumidityChartSelection(
                max = if (sets.isMaxAllowed) points.getOrNull(index)
                    .also { increaseIndex() } else null,
                avg = if (sets.isAvgAllowed) points.getOrNull(index)
                    .also { increaseIndex() } else null,
                min = if (sets.isMinAllowed) points.getOrNull(index)
                    .also { increaseIndex() } else null,
                date = date,
            )
        } else null
    }

    private suspend fun fetchTabData(
        tab: HistoryTab,
        tempChartSets: TempChartSets,
        pressureChartSets: PressureSets,
        humidityChartSets: HumidityChartSets,
        startDate: LocalDate? = null,
    ): HistoryTabState {
        return withContext(defaultDispatcher) {
            when (tab) {
                HistoryTab.YESTERDAY -> historyUseCase.getYesterdayHistory()
                HistoryTab.WEEK -> historyUseCase.getWeekHistory(startDate)
                HistoryTab.MONTH -> historyUseCase.getMonthHistory(startDate)
            }
        }.toTabData(tempChartSets, pressureChartSets, humidityChartSets, tab)
    }
}

fun History.toTabData(
    tempChartSets: TempChartSets,
    pressureSets: PressureSets,
    humidityChartSets: HumidityChartSets,
    tab: HistoryTab,
): HistoryTabState {
    Logger.d("Loaded observations $this")
    with(this.observations) {
        val prescriptionForPeriod = sumOf { it.metric.precipTotal.orZero }
        val windSpeedMax = maxByOrNull { it.metric.windspeedHigh.orZero }
        val startDate =
            this.minByOrNull { it.obsTimeUtc.toEpochDays() }?.obsTimeUtc?.toLocalDateTime(
                TimeZone.UTC
            )?.date ?: firstDate
        val xOffset =
            if (startDate <= firstDate) 0f else firstDate.until(startDate, DateTimeUnit.DAY)
                .toFloat()

        return HistoryTabState(
            startDate = startDate,
            uv = createUvState(startDate),
            temperature = createTemperatureState(
                startDate,
                tempChartSets,
                tab,
                xOffset
            ),
            pressure = createPressureState(
                startDate,
                pressureSets,
                tab,
                xOffset
            ),
            humidity = createHumidityState(humidityChartSets, tab, xOffset),
            maxWindSpeed = windSpeedMax?.metric?.windspeedHigh ?: 0.0,
            maxWindSpeedDate = windSpeedMax?.dateTimeLocal?.date ?: startDate,
            prescriptionForPeriod = prescriptionForPeriod,
        )
    }
}

private fun List<HistoryObservation>.createUvState(
    startDate: LocalDate
): UvState {
    val maxUV = maxByOrNull { it.uvHigh.orZero }
    val maxRadiation = maxByOrNull { it.solarRadiationHigh.orZero }

    return UvState(
        maxUvIndex = maxUV?.uvHigh?.toInt() ?: 0,
        maxRadiation = maxRadiation?.solarRadiationHigh ?: 0.0,
        maxUvDate = maxUV?.dateTimeLocal?.date ?: startDate,
        maxRadiationDate = maxRadiation?.dateTimeLocal?.date ?: startDate,
    )
}

fun History.createHumidityState(
    humidityChartSets: HumidityChartSets,
    tab: HistoryTab,
    xOffset: Float
): ChartState<HumidityChartSelection, HumidityChartSets> {
    val maxHumidities =
        observations.map { it.dateTimeLocal.date to it.humidityHigh.orZero }
            .sortedBy { it.first.toEpochDays() }
    val avgHumidities =
        observations.map { it.dateTimeLocal.date to it.humidityAvg.orZero }
            .sortedBy { it.first.toEpochDays() }
    val minHumidities =
        observations.map { it.dateTimeLocal.date to it.humidityLow.orZero }
            .sortedBy { it.first.toEpochDays() }
    val humidityModel = listOfNotNull(
        maxHumidities.takeIf { humidityChartSets.isMaxAllowed },
        avgHumidities.takeIf { humidityChartSets.isAvgAllowed },
        minHumidities.takeIf { humidityChartSets.isMinAllowed },
    )

    return ChartState(
        observations = observations,
        chartSets = humidityChartSets,
        selectedEntries = null,
        bottomLabels = bottomLabels(tab),
        startDate = firstDate,
        endDate = lastDate,
        chartModel = humidityModel.toChartModel(tab.daysCount, 0f, 100f, xOffset)
    )
}

private fun History.createTemperatureState(
    startDate: LocalDate,
    tempChartSets: TempChartSets,
    tab: HistoryTab,
    xOffset: Float
): TemperatureState {
    val minTemperature = observations.minByOrNull { it.metric.tempLow.orZero }
    val maxTemperature = observations.maxByOrNull { it.metric.tempHigh.orZero }
    val maxTemperatures =
        observations.map { it.dateTimeLocal.date to it.metric.tempHigh.orZero }
            .sortedBy { it.first.toEpochDays() }
    val avgTemperatures =
        observations.map { it.dateTimeLocal.date to it.metric.tempAvg.orZero }
            .sortedBy { it.first.toEpochDays() }
    val minTemperatures =
        observations.map { it.dateTimeLocal.date to it.metric.tempLow.orZero }
            .sortedBy { it.first.toEpochDays() }
    val tempChartModel = listOfNotNull(
        maxTemperatures.takeIf { tempChartSets.isMaxAllowed },
        avgTemperatures.takeIf { tempChartSets.isAvgAllowed },
        minTemperatures.takeIf { tempChartSets.isMinAllowed },
    )

    return TemperatureState(
        maxTemperature = maxTemperature?.metric?.tempHigh ?: 0.0,
        minTemperature = minTemperature?.metric?.tempLow ?: 0.0,
        maxDate = maxTemperature?.dateTimeLocal?.date ?: startDate,
        minDate = minTemperature?.dateTimeLocal?.date ?: startDate,
        chart = ChartState(
            observations = observations,
            chartSets = tempChartSets,
            selectedEntries = null,
            bottomLabels = bottomLabels(tab),
            startDate = firstDate,
            endDate = lastDate,
            chartModel = tempChartModel.toChartModel(tab.daysCount, 0f, 25f, xOffset)
        )
    )
}

private fun History.createPressureState(
    startDate: LocalDate,
    pressureSets: PressureSets,
    tab: HistoryTab,
    xOffset: Float
): PressureState {
    val maxPressure = observations.maxByOrNull { it.metric.pressureMax.orZero }
    val minPressure = observations.minByOrNull { it.metric.pressureMin.orZero }
    val maxPressures =
        observations.map { it.dateTimeLocal.date to it.metric.pressureMax.orZero }
            .sortedBy { it.first.toEpochDays() }
    val minPressures =
        observations.map { it.dateTimeLocal.date to it.metric.pressureMin.orZero }
            .sortedBy { it.first.toEpochDays() }
    val pressureModel = listOfNotNull(
        maxPressures.takeIf { pressureSets.isMaxAllowed },
        minPressures.takeIf { pressureSets.isMinAllowed },
    )

    return PressureState(
        maxPressure = maxPressure?.metric?.pressureMax ?: 0.0,
        maxPressureDate = maxPressure?.dateTimeLocal?.date ?: startDate,
        minPressure = minPressure?.metric?.pressureMin ?: 0.0,
        minPressureDate = maxPressure?.dateTimeLocal?.date ?: startDate,
        trends = observations.map { it.metric.pressureTrend.orZero },
        chart = ChartState(
            observations = observations,
            chartSets = pressureSets,
            selectedEntries = null,
            bottomLabels = bottomLabels(tab),
            startDate = firstDate,
            endDate = lastDate,
            chartModel = pressureModel.toChartModel(tab.daysCount, 1000f, 1025f, xOffset)
        ),
    )
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

    fun selectMaxHumidity(isSelected: Boolean)

    fun selectMinHumidity(isSelected: Boolean)

    fun selectAvgHumidity(isSelected: Boolean)

    fun selectTempPoints(points: List<ChartPointEntry>, dayIndex: Int)

    fun selectPressurePoints(points: List<ChartPointEntry>, dayIndex: Int)

    fun selectHumidityPoints(points: List<ChartPointEntry>, dayIndex: Int)
}

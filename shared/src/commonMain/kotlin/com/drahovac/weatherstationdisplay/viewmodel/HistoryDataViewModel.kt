package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.HistoryObservation
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
                        HistoryTab.WEEK to fetchTabData(HistoryTab.WEEK, TempChartSets())
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
                    currentState.tabData[tab] ?: fetchTabData(tab, TempChartSets())
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
            val tempChartSets = it.currentTabData?.tempChartSets?.copy(isMaxAllowed = isSelected)
            val tabData = getTabData(it, tempChartSets ?: TempChartSets(isMaxAllowed = isSelected))
            it.copy(tabData = tabData)
        }
    }

    private fun getTabData(
        state: HistoryDataState,
        tempChartSets: TempChartSets
    ): MutableMap<HistoryTab, HistoryTabState?> {
        return state.tabData.toMutableMap().apply {
            val removed = remove(state.selectedTab)
            put(
                state.selectedTab,
                removed?.observations?.toTabData(tempChartSets, state.selectedTab.daysCount)
            )
        }
    }

    override fun selectMinTempChart(isSelected: Boolean) {
        _state.update {
            val tempChartSets = it.currentTabData?.tempChartSets?.copy(isMinAllowed = isSelected)
            val tabData = getTabData(it, tempChartSets ?: TempChartSets(isMinAllowed = isSelected))
            it.copy(tabData = tabData)
        }
    }

    override fun selectAvgTempChart(isSelected: Boolean) {
        _state.update {
            val tempChartSets = it.currentTabData?.tempChartSets?.copy(isAvgAllowed = isSelected)
            val tabData = getTabData(it, tempChartSets ?: TempChartSets(isAvgAllowed = isSelected))
            it.copy(tabData = tabData)
        }
    }

    private suspend fun fetchTabData(
        tab: HistoryTab,
        tempChartSets: TempChartSets,
    ): HistoryTabState? {
        return withContext(defaultDispatcher) {
            when (tab) {
                HistoryTab.YESTERDAY -> historyUseCase.getYesterdayHistory()
                HistoryTab.WEEK -> historyUseCase.getWeekHistory()
                HistoryTab.MONTH -> historyUseCase.getMonthHistory()
            }
        }.toTabData(tempChartSets, tab.daysCount)
    }
}

fun List<HistoryObservation>.toTabData(
    tempChartSets: TempChartSets,
    defaultDaysCount: Float,
): HistoryTabState? {
    if (isEmpty()) return null
    val minTemperature = minBy { it.metric.tempLow }
    val maxTemperature = maxBy { it.metric.tempHigh }
    val maxTemperatures =
        map { it.dateTimeLocal.date to it.metric.tempHigh }.sortedBy { it.first.toEpochDays() }
    val avgTemperatures =
        map { it.dateTimeLocal.date to it.metric.tempAvg }.sortedBy { it.first.toEpochDays() }
    val minTemperatures =
        map { it.dateTimeLocal.date to it.metric.tempLow }.sortedBy { it.first.toEpochDays() }
    val tempChartModel = listOfNotNull(
        maxTemperatures.takeIf { tempChartSets.isMaxAllowed },
        avgTemperatures.takeIf { tempChartSets.isAvgAllowed },
        minTemperatures.takeIf { tempChartSets.isMinAllowed },
    )
    // TODO compute min and max here
    return HistoryTabState(
        maxTemperature = maxTemperature.metric.tempHigh,
        minTemperature = minTemperature.metric.tempLow,
        maxDate = maxTemperature.dateTimeLocal.date,
        minDate = minTemperature.dateTimeLocal.date,
        observations = this,
        tempChartSets = tempChartSets,
        tempChartModel = tempChartModel.toChartModel(defaultDaysCount),
    )
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

    fun selectAvgTempChart(isSelected: Boolean)
}

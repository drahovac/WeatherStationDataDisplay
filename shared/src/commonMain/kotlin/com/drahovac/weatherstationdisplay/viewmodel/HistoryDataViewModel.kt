package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.HistoryObservation
import com.drahovac.weatherstationdisplay.usecase.HistoryUseCase
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.coroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HistoryDataViewModel(
    private val historyUseCase: HistoryUseCase,
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
                    selectedTab = HistoryDataTab.WEEK,
                    tabData = mapOf(
                        HistoryDataTab.WEEK to fetchTabData(HistoryDataTab.WEEK)
                    )
                )
            }
        }
    }

    override fun selectTab(tab: HistoryDataTab) {
        _state.update {
            it.copy(selectedTab = tab)
        }
        viewModelScope.coroutineScope.launch {
            _state.update {
                val tabData = it.tabData[tab] ?: fetchTabData(tab)
                val tabMap = it.tabData.toMutableMap().apply {
                    put(tab, tabData)
                }
                it.copy(
                    isLoading = false,
                    selectedTab = tab,
                    tabData = tabMap
                )
            }
        }
    }

    private suspend fun fetchTabData(tab: HistoryDataTab): HistoryTabData? {
        return when (tab) {
            HistoryDataTab.YESTERDAY -> historyUseCase.getYesterdayHistory()
            HistoryDataTab.WEEK -> historyUseCase.getWeekHistory()
            HistoryDataTab.MONTH -> historyUseCase.getMonthHistory()
        }.toTabData()
    }
}

private fun List<HistoryObservation>.toTabData(): HistoryTabData? {
    if (isEmpty()) return null
    val minTemperature = minBy { it.metric.tempLow }
    val maxTemperature = maxBy { it.metric.tempHigh }
    return HistoryTabData(
        maxTemperature = maxTemperature.metric.tempHigh,
        minTemperature = minTemperature.metric.tempLow,
        maxDate = maxTemperature.dateTimeLocal.date,
        minDate = minTemperature.dateTimeLocal.date
    )
}

interface HistoryDataActions {
    fun selectTab(tab: HistoryDataTab)
}

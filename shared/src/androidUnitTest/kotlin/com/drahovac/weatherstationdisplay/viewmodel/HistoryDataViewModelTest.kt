package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.historyMetricPrototype
import com.drahovac.weatherstationdisplay.domain.historyObservationPrototype
import com.drahovac.weatherstationdisplay.usecase.HistoryUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HistoryDataViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val historyUseCase: HistoryUseCase = mockk(relaxUnitFun = true)
    private lateinit var historyDataViewModel: HistoryDataViewModel

    @Before
    fun setUp() {
        coEvery { historyUseCase.getWeekHistory() } returns WEEK_HISTORY
        coEvery { historyUseCase.getMonthHistory() } returns MONTH_HISTORY
        coEvery { historyUseCase.getYesterdayHistory() } returns YESTERDAY_HISTORY
        Dispatchers.setMain(dispatcher)
        historyDataViewModel = HistoryDataViewModel(historyUseCase)
    }

    @Test
    fun `fetch history on init`() = runTest(dispatcher) {
        coVerify { historyUseCase.fetchHistoryUpToDate() }
        assertNotNull(historyDataViewModel.state.value.tabData[HistoryDataTab.WEEK])
    }

    @Test
    fun `set week tab data`() = runTest(dispatcher) {
        historyDataViewModel.state.value.tabData[HistoryDataTab.WEEK]!!.let {
            assertEquals(MAX_TEMP, it.maxTemperature)
            assertEquals(MIN_TEMP, it.minTemperature)
        }
    }

    @Test
    fun `do not fetch data if same tab selected`() = runTest(dispatcher) {
        historyDataViewModel.selectTab(HistoryDataTab.WEEK)

        coVerify(exactly = 1) { historyUseCase.getWeekHistory() } // only once on init
    }

    @Test
    fun `fetch month data on month tab selected`() = runTest(dispatcher) {
        historyDataViewModel.selectTab(HistoryDataTab.MONTH)

        testScheduler.advanceTimeBy(1)

        coVerify { historyUseCase.getMonthHistory() }
        testScheduler.advanceTimeBy(1)
        historyDataViewModel.state.value.let {
            assertEquals(HistoryDataTab.MONTH, it.selectedTab)
            assertEquals(MAX_TEMP_MONTH, it.tabData[HistoryDataTab.MONTH]!!.maxTemperature)
        }
    }

    @Test
    fun `fetch yesterday data on yesterday tab selected`() = runTest(dispatcher) {
        historyDataViewModel.selectTab(HistoryDataTab.YESTERDAY)

        testScheduler.advanceTimeBy(1)

        coVerify { historyUseCase.getYesterdayHistory() }
        testScheduler.advanceTimeBy(1)
        historyDataViewModel.state.value.let {
            assertEquals(HistoryDataTab.YESTERDAY, it.selectedTab)
            assertEquals(MAX_TEMP_YESTERDAY, it.tabData[HistoryDataTab.YESTERDAY]!!.maxTemperature)
        }
    }

    private companion object {
        const val MAX_TEMP = 14.0
        const val MAX_TEMP_YESTERDAY = 2.0
        const val MAX_TEMP_MONTH = 23.0
        const val MIN_TEMP = -13.0
        val METRIC1 = historyMetricPrototype.copy(tempHigh = MAX_TEMP_YESTERDAY, tempLow = MIN_TEMP)
        val METRIC2 = historyMetricPrototype.copy(tempHigh = MAX_TEMP, tempLow = 10.0)
        val METRIC3 = historyMetricPrototype.copy(tempHigh = MAX_TEMP_MONTH, tempLow = 10.0)

        val HISTORY1 = historyObservationPrototype.copy(metric = METRIC1)
        val HISTORY2 = historyObservationPrototype.copy(stationID = "ID3", metric = METRIC2)
        val HISTORY3 = historyObservationPrototype.copy(stationID = "ID5", metric = METRIC3)
        val WEEK_HISTORY = listOf(HISTORY1, HISTORY2)
        val MONTH_HISTORY = listOf(HISTORY1, HISTORY2, HISTORY2, HISTORY3)
        val YESTERDAY_HISTORY = listOf(HISTORY1)
    }
}
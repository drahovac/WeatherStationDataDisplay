package com.drahovac.weatherstationdisplay.viewmodel

import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.TestLogWriter
import com.drahovac.weatherstationdisplay.domain.History
import com.drahovac.weatherstationdisplay.domain.historyMetricPrototype
import com.drahovac.weatherstationdisplay.domain.historyObservationPrototype
import com.drahovac.weatherstationdisplay.usecase.HistoryUseCase
import com.patrykandpatrick.vico.core.entry.FloatEntry
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HistoryDataViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val historyUseCase: HistoryUseCase = mockk(relaxUnitFun = true)
    private lateinit var historyDataViewModel: HistoryDataViewModel

    @OptIn(ExperimentalKermitApi::class)
    @Before
    fun setUp() {
        Logger.setLogWriters(TestLogWriter(Severity.Verbose))
        coEvery { historyUseCase.getWeekHistory() } returns WEEK_HISTORY
        coEvery { historyUseCase.getMonthHistory() } returns MONTH_HISTORY
        coEvery { historyUseCase.getYesterdayHistory() } returns YESTERDAY_HISTORY
        Dispatchers.setMain(dispatcher)
        historyDataViewModel = HistoryDataViewModel(historyUseCase, UnconfinedTestDispatcher())
    }

    @Test
    fun `fetch history on init`() = runTest(dispatcher) {
        coVerify { historyUseCase.fetchHistoryUpToDate() }
        assertNotNull(historyDataViewModel.state.value.tabData[HistoryTab.WEEK])
    }

    @Test
    fun `set week tab data`() = runTest(dispatcher) {
        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.let {
            assertEquals(MAX_TEMP, it.temperature.maxTemperature)
            assertEquals(MIN_TEMP, it.temperature.minTemperature)
        }
    }

    @Test
    fun `do not fetch data if same tab selected`() = runTest(dispatcher) {
        historyDataViewModel.selectTab(HistoryTab.WEEK)

        coVerify(exactly = 1) { historyUseCase.getWeekHistory() } // only once on init
    }

    @Test
    fun `fetch month data on month tab selected`() = runTest(dispatcher) {
        historyDataViewModel.selectTab(HistoryTab.MONTH)

        testScheduler.advanceTimeBy(1)

        coVerify { historyUseCase.getMonthHistory() }
        testScheduler.advanceTimeBy(1)
        historyDataViewModel.state.value.let {
            assertEquals(HistoryTab.MONTH, it.selectedTab)
            assertEquals(MAX_TEMP_MONTH, it.tabData[HistoryTab.MONTH]!!.temperature.maxTemperature)
            assertEquals(MAX_RADIATION_MONTH, it.tabData[HistoryTab.MONTH]!!.uv.maxRadiation)
            assertEquals(MAX_UV_MONTH.toInt(), it.tabData[HistoryTab.MONTH]!!.uv.maxUvIndex)
            assertEquals(PRESSURE_MAX, it.tabData[HistoryTab.MONTH]!!.pressure.maxPressure)
            assertEquals(PRESSURE_MIN, it.tabData[HistoryTab.MONTH]!!.pressure.minPressure)
            assertEquals(17f,
                it.tabData[HistoryTab.MONTH]!!.temperature.chart.chartModel.entries.first()
                    .first().x
            )
        }
    }

    @Test
    fun `fetch yesterday data on yesterday tab selected`() = runTest(dispatcher) {
        historyDataViewModel.selectTab(HistoryTab.YESTERDAY)

        testScheduler.advanceTimeBy(1)

        coVerify { historyUseCase.getYesterdayHistory() }
        testScheduler.advanceTimeBy(1)
        historyDataViewModel.state.value.let {
            assertEquals(HistoryTab.YESTERDAY, it.selectedTab)
            assertEquals(
                MAX_TEMP_YESTERDAY,
                it.tabData[HistoryTab.YESTERDAY]!!.temperature.maxTemperature
            )
        }
    }

    @Test
    fun `unselect max temp chart`() = runTest(dispatcher) {
        historyDataViewModel.selectMaxTempChart(false)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.let {
            assertEquals(2, it.temperature.chart.chartModel.entries.size)
            assertFalse(historyDataViewModel.state.value.currentTabData!!.temperature.chart.chartSets.isMaxAllowed)
            assertTrue(historyDataViewModel.state.value.currentTabData!!.temperature.chart.chartSets.isMinAllowed)
            assertTrue(historyDataViewModel.state.value.currentTabData!!.temperature.chart.chartSets.isAvgAllowed)
        }
    }

    @Test
    fun `unselect min temp chart`() = runTest(dispatcher) {
        historyDataViewModel.selectMinTempChart(false)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.let {
            assertEquals(2, it.temperature.chart.chartModel.entries.size)
            assertTrue(historyDataViewModel.state.value.currentTabData!!.temperature.chart.chartSets.isMaxAllowed)
            assertTrue(historyDataViewModel.state.value.currentTabData!!.temperature.chart.chartSets.isAvgAllowed)
            assertFalse(historyDataViewModel.state.value.currentTabData!!.temperature.chart.chartSets.isMinAllowed)
        }
    }

    @Test
    fun `unselect avg temp chart`() = runTest(dispatcher) {
        historyDataViewModel.selectAvgTempChart(false)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.let {
            assertEquals(2, it.temperature.chart.chartModel.entries.size)
            assertTrue(historyDataViewModel.state.value.currentTabData!!.temperature.chart.chartSets.isMaxAllowed)
            assertFalse(historyDataViewModel.state.value.currentTabData!!.temperature.chart.chartSets.isAvgAllowed)
            assertTrue(historyDataViewModel.state.value.currentTabData!!.temperature.chart.chartSets.isMaxAllowed)
        }
    }

    @Test
    fun `set selection if all charts selected`() = runTest(dispatcher) {
        historyDataViewModel.selectTempPoints(listOf(POINT1, POINT2, POINT3), 1)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.temperature.chart.selectedEntries.let { selection ->
            assertNotNull(selection)
            assertEquals(POINT1, selection.maxTemp)
            assertEquals(POINT2, selection.avgTemp)
            assertEquals(POINT3, selection.minTemp)
        }
    }

    @Test
    fun `clear selection if entries empty`() = runTest(dispatcher) {
        historyDataViewModel.selectTempPoints(listOf(POINT1, POINT2, POINT3), 1)
        historyDataViewModel.selectTempPoints(emptyList(), 0)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.temperature.chart.selectedEntries.let { selection ->
            assertNull(selection)
        }
    }

    @Test
    fun `clear selection if sets empty`() = runTest(dispatcher) {
        historyDataViewModel.selectAvgTempChart(false)
        historyDataViewModel.selectMaxTempChart(false)
        historyDataViewModel.selectMinTempChart(false)
        historyDataViewModel.selectTempPoints(listOf(POINT1, POINT2, POINT3), 1)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.temperature.chart.selectedEntries.let { selection ->
            assertNull(selection)
        }
    }

    @Test
    fun `set selection if avg and min charts selected`() = runTest(dispatcher) {
        historyDataViewModel.selectMaxTempChart(false)

        historyDataViewModel.selectTempPoints(listOf(POINT1, POINT2), 0)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.temperature.chart.selectedEntries.let { selection ->
            assertNotNull(selection)
            assertNull(selection.maxTemp)
            assertEquals(POINT1, selection.avgTemp)
            assertEquals(POINT2, selection.minTemp)
        }
    }

    @Test
    fun `set selection if max and min charts selected`() = runTest(dispatcher) {
        historyDataViewModel.selectAvgTempChart(false)

        historyDataViewModel.selectTempPoints(listOf(POINT1, POINT2), 0)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.temperature.chart.selectedEntries.let { selection ->
            assertNotNull(selection)
            assertNull(selection.avgTemp)
            assertEquals(POINT1, selection.maxTemp)
            assertEquals(POINT2, selection.minTemp)
        }
    }

    @Test
    fun `set selection date based on index`() = runTest(dispatcher) {
        historyDataViewModel.selectAvgTempChart(false)

        historyDataViewModel.selectTempPoints(listOf(POINT1, POINT2), 4)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.temperature.chart.selectedEntries.let { selection ->
            assertNotNull(selection)
            assertEquals(LocalDate.parse("2023-08-22"), selection.date)
        }
    }

    @Test
    fun `set selection if only min chart selected`() = runTest(dispatcher) {
        historyDataViewModel.selectMaxTempChart(false)
        historyDataViewModel.selectAvgTempChart(false)

        historyDataViewModel.selectTempPoints(listOf(POINT1), 1)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.temperature.chart.selectedEntries.let { selection ->
            assertNotNull(selection)
            assertNull(selection.maxTemp)
            assertNull(selection.avgTemp)
            assertEquals(POINT1, selection.minTemp)
        }
    }

    @Test
    fun `unselect max pressure chart`() = runTest(dispatcher) {
        historyDataViewModel.selectMaxPressureChart(false)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.let {
            assertEquals(1, it.pressure.chart.chartModel.entries.size)
            assertFalse(historyDataViewModel.state.value.currentTabData!!.pressure.chart.chartSets.isMaxAllowed)
            assertTrue(historyDataViewModel.state.value.currentTabData!!.pressure.chart.chartSets.isMinAllowed)
        }
    }

    @Test
    fun `select max pressure chart`() = runTest(dispatcher) {
        historyDataViewModel.selectMaxPressureChart(false)

        historyDataViewModel.selectMaxPressureChart(true)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.let {
            assertEquals(2, it.pressure.chart.chartModel.entries.size)
            assertTrue(historyDataViewModel.state.value.currentTabData!!.pressure.chart.chartSets.isMaxAllowed)
            assertTrue(historyDataViewModel.state.value.currentTabData!!.pressure.chart.chartSets.isMinAllowed)
        }
    }

    @Test
    fun `unselect min pressure chart`() = runTest(dispatcher) {
        historyDataViewModel.selectMinPressureChart(false)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.let {
            assertEquals(1, it.pressure.chart.chartModel.entries.size)
            assertTrue(historyDataViewModel.state.value.currentTabData!!.pressure.chart.chartSets.isMaxAllowed)
            assertFalse(historyDataViewModel.state.value.currentTabData!!.pressure.chart.chartSets.isMinAllowed)
        }
    }

    @Test
    fun `select min pressure chart`() = runTest(dispatcher) {
        historyDataViewModel.selectMinPressureChart(false)

        historyDataViewModel.selectMinPressureChart(true)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.let {
            assertEquals(2, it.pressure.chart.chartModel.entries.size)
            assertTrue(historyDataViewModel.state.value.currentTabData!!.pressure.chart.chartSets.isMaxAllowed)
            assertTrue(historyDataViewModel.state.value.currentTabData!!.pressure.chart.chartSets.isMinAllowed)
        }
    }

    @Test
    fun `set selection if all pressure charts selected`() = runTest(dispatcher) {
        historyDataViewModel.selectPressurePoints(listOf(POINT1, POINT2), 1)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.pressure.chart.selectedEntries.let { selection ->
            assertNotNull(selection)
            assertEquals(POINT1, selection.maxPressure)
            assertEquals(POINT2, selection.minPressure)
            assertEquals(-2.3, selection.trend)
        }
    }

    @Test
    fun `set selection if only min pressures chart selected`() = runTest(dispatcher) {
        historyDataViewModel.selectMaxPressureChart(false)

        historyDataViewModel.selectPressurePoints(listOf(POINT1), 1)

        historyDataViewModel.state.value.tabData[HistoryTab.WEEK]!!.pressure.chart.selectedEntries.let { selection ->
            assertNotNull(selection)
            assertNull(selection.maxPressure)
            assertNull(selection.trend)
            assertEquals(POINT1, selection.minPressure)
        }
    }

    @Test
    fun `fetch month data on select next month`() = runTest(dispatcher) {
        val expectedStart = LocalDate.parse("2023-09-01")
        coEvery { historyUseCase.getMonthHistory(expectedStart) } returns MONTH_HISTORY
        historyDataViewModel.selectNextMonth()

        testScheduler.advanceTimeBy(1)

        coVerify { historyUseCase.getMonthHistory(expectedStart) }
        testScheduler.advanceTimeBy(1)
    }

    @Test
    fun `fetch month data on select previous month`() = runTest(dispatcher) {
        val expectedStart = LocalDate.parse("2023-07-01")
        coEvery { historyUseCase.getMonthHistory(expectedStart) } returns MONTH_HISTORY
        historyDataViewModel.selectPreviousMonth()

        testScheduler.advanceTimeBy(1)

        coVerify { historyUseCase.getMonthHistory(expectedStart) }
        testScheduler.advanceTimeBy(1)
    }


    @Test
    fun `fetch week data on select next week`() = runTest(dispatcher) {
        val expectedStart = LocalDate.parse("2023-08-21")
        coEvery { historyUseCase.getWeekHistory(expectedStart) } returns WEEK_HISTORY

        historyDataViewModel.selectNextWeek()
        testScheduler.advanceTimeBy(1)

        coVerify { historyUseCase.getWeekHistory(expectedStart) }
        testScheduler.advanceTimeBy(1)
    }

    @Test
    fun `fetch week data on select previous week`() = runTest(dispatcher) {
        val expectedStart = LocalDate.parse("2023-08-07")
        coEvery { historyUseCase.getWeekHistory(expectedStart) } returns WEEK_HISTORY

        historyDataViewModel.selectPreviousWeek()
        testScheduler.advanceTimeBy(1)

        coVerify { historyUseCase.getWeekHistory(expectedStart) }
        testScheduler.advanceTimeBy(1)
    }

    private companion object {
        const val MAX_TEMP = 14.0
        const val MAX_TEMP_YESTERDAY = 2.0
        const val MAX_TEMP_MONTH = 23.0
        const val MAX_UV_MONTH = 12.0
        const val MAX_RADIATION_MONTH = 20000.0
        const val MIN_TEMP = -13.0
        const val PRESSURE_MAX = 1500.0
        const val PRESSURE_MIN = 15.0
        val METRIC1 = historyMetricPrototype.copy(tempHigh = MAX_TEMP_YESTERDAY, tempLow = MIN_TEMP)
        val METRIC2 = historyMetricPrototype.copy(
            tempHigh = MAX_TEMP,
            tempLow = 10.0,
            pressureMax = PRESSURE_MAX,
            pressureMin = PRESSURE_MIN
        )
        val METRIC3 = historyMetricPrototype.copy(tempHigh = MAX_TEMP_MONTH, tempLow = 10.0)
        val TODAY = LocalDateTime.parse("2023-08-18T10:34:56").toInstant(TimeZone.UTC) // Friday

        val HISTORY1 = historyObservationPrototype.copy(
            obsTimeUtc = TODAY,
            obsTimeLocal = "2023-08-18T10:34:56",
            metric = METRIC1
        )
        val HISTORY2 = historyObservationPrototype.copy(
            obsTimeUtc = TODAY,
            obsTimeLocal = "2023-08-18T10:34:56",
            stationID = "ID3",
            metric = METRIC2
        )
        val HISTORY3 = historyObservationPrototype.copy(
            obsTimeUtc = TODAY,
            obsTimeLocal = "2023-08-18T10:34:56",
            stationID = "ID5",
            metric = METRIC3,
            uvHigh = MAX_UV_MONTH,
            solarRadiationHigh = MAX_RADIATION_MONTH
        )
        val MONDAY = LocalDate.parse("2023-08-14")
        val SUNDAY = LocalDate.parse("2023-08-20")
        val MONTH_FIRST = LocalDate.parse("2023-08-01")
        val MONTH_LAST = LocalDate.parse("2023-08-31")
        val WEEK_HISTORY = History(MONDAY, SUNDAY, listOf(HISTORY1, HISTORY2))
        val MONTH_HISTORY =
            History(MONTH_FIRST, MONTH_LAST, listOf(HISTORY1, HISTORY2, HISTORY2, HISTORY3))
        val YESTERDAY_HISTORY = History(MONDAY, MONDAY, listOf(HISTORY1))
        val POINT1 = FloatEntry(1f, 1f)
        val POINT2 = FloatEntry(2f, 1f)
        val POINT3 = FloatEntry(3f, 1f)
    }
}
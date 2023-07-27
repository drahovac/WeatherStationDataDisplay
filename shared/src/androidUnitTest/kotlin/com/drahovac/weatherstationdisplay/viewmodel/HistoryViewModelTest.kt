package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.domain.HistoryWeatherDataRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.LocalDate
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class HistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val historyWeatherDataRepository: HistoryWeatherDataRepository =
        mockk(relaxUnitFun = true)
    private lateinit var historyViewModel: HistoryViewModel
    private val stateValue
        get() = historyViewModel.state.value

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        coEvery { historyWeatherDataRepository.fetchHistory(START_DATE) } returns Result.success(
            emptyList()
        )
        Dispatchers.setMain(testDispatcher)
        historyViewModel = HistoryViewModel(historyWeatherDataRepository)
    }

    @Test
    fun `switch date dialog to visible`() {
        historyViewModel.switchDateDialog()

        assertTrue { stateValue.noData?.isPickerVisible!! }
    }

    @Test
    fun `switch date dialog to invisible`() {
        historyViewModel.switchDateDialog()
        historyViewModel.switchDateDialog()

        assertFalse { stateValue.noData?.isPickerVisible!! }
    }

    @Test
    fun `close picker on select start date`() {
        historyViewModel.switchDateDialog()

        historyViewModel.selectStartDate(START_DATE)

        assertFalse { stateValue.noData?.isPickerVisible!! }
    }

    @Test
    fun `select start date`() {
        historyViewModel.selectStartDate(START_DATE)

        assertEquals(START_DATE, stateValue.noData!!.startDate)
    }

    @Test
    fun `set error on download initial history if empty date`() {
        historyViewModel.downloadInitialHistory()

        assertEquals(MR.strings.setup_must_not_be_empty, stateValue.noData!!.error)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `fetch data on download initial history`() {
        historyViewModel.selectStartDate(START_DATE)

        historyViewModel.downloadInitialHistory()
        testDispatcher.scheduler.advanceTimeBy(1)

        coVerify { historyWeatherDataRepository.fetchHistory(START_DATE) }
        assertFalse { stateValue.isLoading }
    }

    @Test
    fun `set loading state on download initial history`() {
        historyViewModel.selectStartDate(START_DATE)

        historyViewModel.downloadInitialHistory()

        assertTrue { stateValue.isLoading }
    }

    @Test
    fun `clear error on select date`() {
        historyViewModel.downloadInitialHistory()

        historyViewModel.selectStartDate(START_DATE)

        assertNull(stateValue.noData!!.error)
    }

    private companion object {
        val START_DATE = LocalDate.parse("2023-01-01")
    }
}
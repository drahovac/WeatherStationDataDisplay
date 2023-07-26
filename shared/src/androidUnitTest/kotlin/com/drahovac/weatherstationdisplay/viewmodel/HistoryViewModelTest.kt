package com.drahovac.weatherstationdisplay.viewmodel

import kotlinx.datetime.LocalDate
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class HistoryViewModelTest {

    private val historyViewModel: HistoryViewModel = HistoryViewModel()
    private val stateValue
        get() = historyViewModel.state.value

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

    private companion object {
        val START_DATE = LocalDate.parse("2023-01-01")
    }
}
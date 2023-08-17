package com.drahovac.weatherstationdisplay.viewmodel

import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.FloatEntry
import org.junit.Test
import kotlin.test.assertEquals

class ChartModelTest {

    val model =
        ChartModel(listOf(LineChartEntryModel(ENTRIES_1), LineChartEntryModel(ENTRIES_2)), 6f)

    @Test
    fun `return correct min max values`() {
        assertEquals(0f, model.minX)
        assertEquals(6f, model.maxX)
        assertEquals(20f, model.maxY)
        assertEquals(-5f, model.minY)
    }

    @Test
    fun `return valid model with empty values`() {
        val emptyModel = ChartModel(emptyList(), 5f)

        assertEquals(0f, emptyModel.minX)
        assertEquals(5f, emptyModel.maxX)
        assertEquals(25f, emptyModel.maxY)
        assertEquals(0f, emptyModel.minY)
    }

    private companion object {
        val ENTRY_1 = FloatEntry(3f, 4f)
        val ENTRY_2 = FloatEntry(21f, 13f)
        val ENTRY_3 = FloatEntry(30f, 8f)
        val ENTRY_4 = FloatEntry(40f, 11f)
        val ENTRY_5 = FloatEntry(19f, 14f)
        val ENTRY_6 = FloatEntry(15f, 17f)
        val ENTRIES_1: List<ChartEntry> = listOf(ENTRY_1, ENTRY_2, ENTRY_3)
        val ENTRIES_2: List<ChartEntry> = listOf(ENTRY_4, ENTRY_5, ENTRY_6)
    }
}

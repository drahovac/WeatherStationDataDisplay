package com.drahovac.weatherstationdisplay.viewmodel

import org.junit.Test
import kotlin.test.assertEquals

internal class SetupDeviceIdViewModelTest {

    private val viewModel: SetupDeviceIdViewModel = SetupDeviceIdViewModel()

    @Test
    fun `set device id`() {
        viewModel.setDeviceId(DEVICE_ID)

        assertEquals(DEVICE_ID, viewModel.state.value)
    }

    private companion object {
        const val DEVICE_ID = "DEVICE_ID"
    }
}
package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.Destination
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class NavigationViewModelTest {

    private class TestNavigationViewModel : NavigationViewModel() {
        fun setDest() {
            this.setDestination(DESTINATION)
        }
    }

    private val navigationViewModel = TestNavigationViewModel()

    @Test
    fun `should send destination event only once`() {
        navigationViewModel.setDest()

        assertEquals(DESTINATION, navigationViewModel.navigationFlow.value.receive())
        assertNull(navigationViewModel.navigationFlow.value.receive())
    }

    private companion object {
        val DESTINATION = Destination.SetupApiKey
    }
}
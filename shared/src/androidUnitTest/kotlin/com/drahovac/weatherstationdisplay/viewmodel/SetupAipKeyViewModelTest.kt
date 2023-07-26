package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.domain.Destination
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class SetupAipKeyViewModelTest {
    private val credentialsRepository: DeviceCredentialsRepository = mockk(relaxUnitFun = true)
    private lateinit var viewModel: SetupAipKeyViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = SetupAipKeyViewModel(credentialsRepository)
    }

    @Test
    fun `set device id`() {
        viewModel.setValue(API_KEY)

        assertEquals(API_KEY, viewModel.state.value.value)
    }

    @Test
    fun `set error on save if device id is empty`() {
        viewModel.setValue(API_KEY)
        viewModel.setValue("")

        viewModel.saveValue()

        assertEquals(MR.strings.setup_must_not_be_empty, viewModel.state.value.error)
    }

    @Test
    fun `clear error on save if device id is not empty`() {
        viewModel.setValue("")

        viewModel.saveValue()
        viewModel.setValue(API_KEY)
        viewModel.saveValue()

        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `do not save device id if is empty`() {
        viewModel.setValue(API_KEY)
        viewModel.setValue("")

        viewModel.saveValue()

        coVerify(exactly = 0) { credentialsRepository.saveApiKey(any()) }
        assertNull(viewModel.navigationFlow.value.receive())
    }

    @Test
    fun `save device id and navigate to next screen`() {
        viewModel.setValue(API_KEY)

        viewModel.saveValue()

        coVerify { credentialsRepository.saveApiKey(API_KEY) }
        assertEquals(Destination.CurrentWeather, viewModel.navigationFlow.value.receive())
    }

    private companion object {
        const val API_KEY = "API_KEY"
    }
}

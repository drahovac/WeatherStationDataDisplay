package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.domain.Destination
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class SetupDeviceIdViewModelTest {

    private val credentialsRepository: DeviceCredentialsRepository = mockk(relaxUnitFun = true)
    private lateinit var viewModel: SetupDeviceIdViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        coEvery { credentialsRepository.getApiKey() } returns null
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = SetupDeviceIdViewModel(credentialsRepository)
    }

    @Test
    fun `set device id`() {
        viewModel.setValue(DEVICE_ID)

        assertEquals(DEVICE_ID, viewModel.state.value.value)
    }

    @Test
    fun `set error on save if device id is empty`() {
        viewModel.setValue(DEVICE_ID)
        viewModel.setValue("")

        viewModel.saveValue()

        assertEquals(MR.strings.setup_must_not_be_empty, viewModel.state.value.error)
    }

    @Test
    fun `clear error on save if device id is not empty`() {
        viewModel.setValue("")

        viewModel.saveValue()
        viewModel.setValue(DEVICE_ID)
        viewModel.saveValue()

        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `do not save device id if is empty`() {
        viewModel.setValue(DEVICE_ID)
        viewModel.setValue("")

        viewModel.saveValue()

        coVerify(exactly = 0) { credentialsRepository.saveDeviceId(any()) }
        assertNull(viewModel.navigationFlow.value.receive())
    }

    @Test
    fun `save device id and navigate to api screen if key empty`() {
        coEvery { credentialsRepository.getApiKey() } returns null
        viewModel.setValue(DEVICE_ID)

        viewModel.saveValue()

        coVerify { credentialsRepository.saveDeviceId(DEVICE_ID) }
        assertEquals(Destination.SetupApiKey, viewModel.navigationFlow.value.receive())
    }

    @Test
    fun `save device id and navigate to weather screen if key present`() {
        coEvery { credentialsRepository.getApiKey() } returns "ApiKEy"
        viewModel.setValue(DEVICE_ID)

        viewModel.saveValue()

        coVerify { credentialsRepository.saveDeviceId(DEVICE_ID) }
        assertEquals(Destination.CurrentWeather, viewModel.navigationFlow.value.receive())
    }

    private companion object {
        const val DEVICE_ID = "DEVICE_ID"
    }
}

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

internal class SetupDeviceIdViewModelTest {

    private val credentialsRepository: DeviceCredentialsRepository = mockk(relaxUnitFun = true)
    private lateinit var viewModel: SetupDeviceIdViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = SetupDeviceIdViewModel(credentialsRepository)
    }

    @Test
    fun `set device id`() {
        viewModel.setDeviceId(DEVICE_ID)

        assertEquals(DEVICE_ID, viewModel.state.value.id)
    }

    @Test
    fun `set error on save if device id is empty`() {
        viewModel.setDeviceId(DEVICE_ID)
        viewModel.setDeviceId("")

        viewModel.saveDeviceId()

        assertEquals(MR.strings.setup_must_not_be_empty.resourceId, viewModel.state.value.error)
    }

    @Test
    fun `clear error on save if device id is not empty`() {
        viewModel.setDeviceId("")

        viewModel.saveDeviceId()
        viewModel.setDeviceId(DEVICE_ID)
        viewModel.saveDeviceId()

        assertNull(viewModel.state.value.error)
    }

    @Test
    fun `do not save device id if is empty`() {
        viewModel.setDeviceId(DEVICE_ID)
        viewModel.setDeviceId("")

        viewModel.saveDeviceId()

        coVerify(exactly = 0) { credentialsRepository.saveDeviceId(any()) }
        assertNull(viewModel.navigationFlow.value.receive())
    }

    @Test
    fun `save device id and navigate to next screen`() {
        viewModel.setDeviceId(DEVICE_ID)

        viewModel.saveDeviceId()

        coVerify { credentialsRepository.saveDeviceId(DEVICE_ID) }
        assertEquals(Destination.SetupApiKey, viewModel.navigationFlow.value.receive())
    }

    private companion object {
        const val DEVICE_ID = "DEVICE_ID"
    }
}

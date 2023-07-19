package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.MR
import com.drahovac.weatherstationdisplay.domain.Destination
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.rickclephas.kmm.viewmodel.coroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SetupDeviceIdViewModel(
    private val credentialsRepository: DeviceCredentialsRepository
) : NavigationViewModel(), SetupDeviceIdActions {

    private val _state: MutableStateFlow<SetupDeviceIdState> =
        MutableStateFlow(SetupDeviceIdState())

    @NativeCoroutines
    val state = _state.asStateFlow()

    override fun setDeviceId(deviceId: String) {
        _state.update { it.copy(id = deviceId, error = null) }
    }

    override fun saveDeviceId() {
        _state.value.id.orEmpty().let { id ->
            if (id.isEmpty()) {
                _state.update { it.copy(error = MR.strings.setup_must_not_be_empty.resourceId) }
            } else {
                _state.update { it.copy(error = null) }
                viewModelScope.coroutineScope.launch {
                    credentialsRepository.saveDeviceId(id)
                    setDestination(Destination.SetupApiKey)
                }
            }
        }
    }
}

interface SetupDeviceIdActions {

    fun setDeviceId(deviceId: String)

    fun saveDeviceId()
}

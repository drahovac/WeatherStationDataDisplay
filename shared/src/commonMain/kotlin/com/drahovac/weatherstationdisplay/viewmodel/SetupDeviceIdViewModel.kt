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
) : NavigationViewModel(), SetupActions {

    private val _state: MutableStateFlow<SetupState> =
        MutableStateFlow(SetupState())

    @NativeCoroutines
    val state = _state.asStateFlow()

    override fun setValue(value: String) {
        _state.update { it.copy(value = value, error = null) }
    }

    override fun saveValue() {
        _state.value.value.orEmpty().let { id ->
            if (id.isEmpty()) {
                _state.update { it.copy(error = MR.strings.setup_must_not_be_empty) }
            } else {
                _state.update { it.copy(error = null) }
                viewModelScope.coroutineScope.launch {
                    credentialsRepository.saveDeviceId(id)
                    setDestination(
                        if (credentialsRepository.getApiKey() == null) {
                            Destination.SetupApiKey
                        } else Destination.CurrentWeather
                    )
                }
            }
        }
    }
}

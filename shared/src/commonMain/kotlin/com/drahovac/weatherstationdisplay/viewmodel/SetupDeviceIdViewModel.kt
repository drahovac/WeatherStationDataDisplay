package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.Destination
import com.rickclephas.kmm.viewmodel.coroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SetupDeviceIdViewModel : NavigationViewModel(), SetupDeviceIdActions {

    private val _state: MutableStateFlow<String?> = MutableStateFlow(null)

    @NativeCoroutines
    val state = _state.asStateFlow()

    override fun setDeviceId(deviceId: String) {
        _state.value = deviceId
    }

    override fun saveDeviceId() {
        // TODO
        viewModelScope.coroutineScope.launch {
            delay(3000L)
            setDestination(Destination.SetupApiKey)
        }
    }
}

interface SetupDeviceIdActions {

    fun setDeviceId(deviceId: String)

    fun saveDeviceId()
}

package com.drahovac.weatherstationdisplay.viewmodel

import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.coroutineScope
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SetupDeviceIdViewModel : KMMViewModel(), SetupDeviceIdActions {

    private val _state: MutableStateFlow<String?> = MutableStateFlow(null)

    @NativeCoroutines
    val state = _state.asStateFlow()

    override fun setDeviceId(deviceId: String) {
        _state.value = deviceId
    }

    override fun saveDeviceId(onSuccess: () -> Unit) {
        // TODO
        viewModelScope.coroutineScope.launch {
            delay(3000L)
            onSuccess()
        }
    }
}

interface SetupDeviceIdActions {

    fun setDeviceId(deviceId: String)

    fun saveDeviceId(onSuccess: () -> Unit)
}

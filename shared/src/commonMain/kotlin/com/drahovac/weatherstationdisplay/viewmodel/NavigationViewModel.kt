package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.Destination
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmm.viewmodel.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

open class NavigationViewModel : KMMViewModel() {

    private val _navigationFlow = MutableStateFlow(ExactlyOnceEventBus<Destination>())
    val navigationFlow = _navigationFlow.asStateFlow()


    protected fun setDestination(destination: Destination) {
        _navigationFlow.value = ExactlyOnceEventBus(destination)
    }
}

open class SecuredNavigationViewModel(
    private val credentialsRepository: DeviceCredentialsRepository,
) : KMMViewModel() {

    private val _navigationFlow = MutableStateFlow(ExactlyOnceEventBus<Destination>())
    val navigationFlow = _navigationFlow.asStateFlow()

    fun onNewApiKey() {
        viewModelScope.coroutineScope.launch {
            credentialsRepository.removeApiKey()
            setDestination(Destination.SetupApiKey)
        }
    }

    fun onNewDeviceId() {
        viewModelScope.coroutineScope.launch {
            credentialsRepository.removeDeviceId()
            setDestination(Destination.SetupDeviceId)
        }
    }

    protected fun setDestination(destination: Destination) {
        _navigationFlow.value = ExactlyOnceEventBus(destination)
    }
}
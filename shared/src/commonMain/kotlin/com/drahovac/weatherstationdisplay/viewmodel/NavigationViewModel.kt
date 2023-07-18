package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.Destination
import com.rickclephas.kmm.viewmodel.KMMViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class NavigationViewModel : KMMViewModel() {

    private val _navigationFlow = MutableStateFlow(ExactlyOnceEventBus<Destination>())
    val navigationFlow = _navigationFlow.asStateFlow()


    protected fun setDestination(destination: Destination) {
        _navigationFlow.value = ExactlyOnceEventBus(destination)
    }
}
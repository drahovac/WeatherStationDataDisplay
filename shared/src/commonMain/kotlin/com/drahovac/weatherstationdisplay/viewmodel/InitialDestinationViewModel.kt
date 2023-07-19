package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.Destination
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository

class InitialDestinationViewModel(
    private val deviceCredentialsRepository: DeviceCredentialsRepository
) {
    suspend fun getInitialDestination(): Destination {
        return when {
            deviceCredentialsRepository.getDeviceId() == null -> Destination.SetupDeviceId
            deviceCredentialsRepository.getApiKey() == null -> Destination.SetupApiKey
            else -> Destination.CurrentWeather
        }
    }
}

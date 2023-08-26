package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CreateSetUpDeviceIdViewModelHelper : KoinComponent {
    val credentialsRepository: DeviceCredentialsRepository by inject()
}

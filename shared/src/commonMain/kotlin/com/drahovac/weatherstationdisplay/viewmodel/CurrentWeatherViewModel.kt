package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.CurrentWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.DeviceCredentialsRepository
import com.drahovac.weatherstationdisplay.domain.networkErrorOrNull
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

class CurrentWeatherViewModel(
    private val currentWeatherDataRepository: CurrentWeatherDataRepository,
    credentialsRepository: DeviceCredentialsRepository
) : SecuredNavigationViewModel(credentialsRepository) {

    private val _state = MutableStateFlow(CurrentWeatherState())

    @NativeCoroutines
    val state = _state.asStateFlow()

    suspend fun observeWeather() {
        while (coroutineContext.isActive) {
            val result = currentWeatherDataRepository.getCurrentData()
            _state.update { CurrentWeatherState(result.getOrNull(), result.networkErrorOrNull()) }
            delay(DELAY)
        }
    }

    private companion object {
        const val DELAY = 15000L
    }
}

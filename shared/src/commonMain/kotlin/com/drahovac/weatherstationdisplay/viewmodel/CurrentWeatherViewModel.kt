package com.drahovac.weatherstationdisplay.viewmodel

import com.drahovac.weatherstationdisplay.domain.CurrentWeatherDataRepository
import com.drahovac.weatherstationdisplay.domain.CurrentWeatherObservation
import com.rickclephas.kmm.viewmodel.KMMViewModel
import com.rickclephas.kmp.nativecoroutines.NativeCoroutines
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

class CurrentWeatherViewModel(
    private val currentWeatherDataRepository: CurrentWeatherDataRepository
) : KMMViewModel() {

    private val _state = MutableStateFlow<CurrentWeatherObservation?>(null)

    @NativeCoroutines
    val state = _state.asStateFlow()

    suspend fun observeWeather() {
        while (coroutineContext.isActive) {
            val result = currentWeatherDataRepository.getCurrentData()
            _state.update { result.getOrNull() }
            delay(DELAY)
        }
    }

    private companion object {
        const val DELAY = 15000L
    }
}

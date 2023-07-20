package com.drahovac.weatherstationdisplay.domain

interface CurrentWeatherDataRepository {

    suspend fun getCurrentData(): Result<CurrentWeatherObservation>
}
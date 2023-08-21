package com.drahovac.weatherstationdisplay.domain

interface ForecastRepository {
    suspend fun fetchForecast(
         language: String,
         geoCode: String,
    ) : Result<Forecast>
}

package com.drahovac.weatherstationdisplay.domain;

/**
 * Destination where to navigate.
 */
enum class Destination {

    SetupDeviceId, SetupApiKey, CurrentWeather, History, Forecast;

    /**
     * String representation of destination used as route in navigation.
     */
    fun route(): String = this.name
}

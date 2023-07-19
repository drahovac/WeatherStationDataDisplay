package com.drahovac.weatherstationdisplay.domain;

/**
 * Destination where to navigate.
 */
sealed interface Destination {

    object SetupDeviceId : Destination

    object SetupApiKey : Destination

    object CurrentWeather : Destination

    /**
     * String representation of destination used as route in navigation.
     */
    fun route(): String = this::class.qualifiedName.orEmpty()
}

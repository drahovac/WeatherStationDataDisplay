package com.drahovac.weatherstationdisplay.domain

sealed class NetworkError : Throwable() {

    object InvalidDeviceId : NetworkError()

    object InvalidApiKey : NetworkError()

    object TooManyRequests : NetworkError()

    data class General(val error: Throwable) : NetworkError()
}

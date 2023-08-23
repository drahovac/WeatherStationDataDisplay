package com.drahovac.weatherstationdisplay.domain

val Double?.orZero: Double
    get() = this ?: 0.0

package com.drahovac.weatherstationdisplay

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
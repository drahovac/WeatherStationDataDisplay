package com.drahovac.weatherstationdisplay.domain

actual fun Locale.language(): String {
    return java.util.Locale.getDefault().language
}
